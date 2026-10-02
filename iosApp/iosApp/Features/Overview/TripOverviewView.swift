import SwiftUI
import SharedLogic

struct TripOverviewView: View {
    private let tripId: String
    private let repository: any TripRepository
    private let appPreferences: any AppPreferences
    private let overviewPreferences: OverviewPreferences
    @State private var showingAddExpense = false
    @State private var showExpenses: Bool
    @State private var viewModel: TripOverviewViewModel

    init(
        tripId: String,
        repository: any TripRepository,
        calculateTripSummary: CalculateTripSummary,
        appPreferences: any AppPreferences,
        overviewPreferences: OverviewPreferences
    ) {
        self.tripId = tripId
        self.repository = repository
        self.appPreferences = appPreferences
        self.overviewPreferences = overviewPreferences

        // Variant A: key and default written per platform. Differs from Android's "show_expenses", true.
        _showExpenses = State(initialValue: appPreferences.getBoolean(key: "showExpenses", defaultValue: false))
        // Variant B: key and default defined once in shared OverviewPreferences.
        // _showExpenses = State(initialValue: overviewPreferences.showExpenses)

        _viewModel = State(initialValue: TripOverviewViewModel(
            tripId: tripId,
            repository: repository,
            calculateTripSummary: calculateTripSummary
        ))
    }

    var body: some View {
        TripOverviewContent(
            state: viewModel.state,
            isLoading: viewModel.isLoading,
            errorMessage: viewModel.errorMessage,
            onRetry: { Task { await viewModel.load() } },
            showExpenses: showExpenses
        )
        .task { await viewModel.load() }
        .refreshable { await viewModel.load() }
        .onChange(of: showExpenses) { _, newValue in
            // Variant A: written under a different key than it is read from above.
            appPreferences.putBoolean(key: "show_expenses", value: newValue)
            // Variant B:
            // overviewPreferences.showExpenses = newValue
        }
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                Toggle("Show expenses", systemImage: "list.bullet", isOn: $showExpenses)
            }
            ToolbarItem(placement: .topBarTrailing) {
                Button { showingAddExpense = true } label: {
                    Label("Add expense", systemImage: "plus")
                }
                .disabled(viewModel.isLoading || viewModel.errorMessage != nil)
            }
        }
        .sheet(isPresented: $showingAddExpense) {
            AddExpenseView(tripId: tripId, repository: repository) {
                Task { await viewModel.load() }
            }
        }
    }
}

private struct TripOverviewContent: View {
    let state: TripOverviewState
    var isLoading = false
    var errorMessage: String? = nil
    var onRetry: () -> Void = {}
    var showExpenses = true

    private var balanceColor: Color {
        if state.balanceCents > 0 { return .green }
        if state.balanceCents < 0 { return .orange }
        return .secondary
    }

    var body: some View {
        List {
            if !isLoading && errorMessage == nil {
                Section {
                    VStack(alignment: .leading, spacing: 16) {
                        Text(state.participantNames)
                            .font(.subheadline)
                            .foregroundStyle(.secondary)

                        VStack(alignment: .leading, spacing: 4) {
                            Text("Total expenses")
                                .font(.subheadline)
                                .foregroundStyle(.secondary)
                            Text(state.totalExpenses)
                                .font(.largeTitle.bold())
                                .monospacedDigit()
                        }

                        Divider()

                        Label(state.balanceText, systemImage: state.balanceCents == 0
                            ? "checkmark.circle" : "arrow.left.arrow.right")
                            .font(.headline)
                            .foregroundStyle(balanceColor)
                    }
                    .padding(.vertical, 8)
                }

                if showExpenses {
                    Section("Expenses") {
                        if state.expenses.isEmpty {
                            ContentUnavailableView(
                                "No expenses yet",
                                systemImage: "receipt",
                                description: Text("Expenses added to this trip will appear here.")
                            )
                        } else {
                            ForEach(state.expenses) { expense in
                                VStack(alignment: .leading, spacing: 6) {
                                    HStack(alignment: .firstTextBaseline) {
                                        Text(expense.description)
                                            .font(.headline)
                                        Spacer()
                                        Text(expense.amount)
                                            .monospacedDigit()
                                            .layoutPriority(1)
                                    }
                                    Text("Paid by \(expense.paidBy)")
                                        .font(.subheadline)
                                        .foregroundStyle(.secondary)
                                    Text(expense.participantCount == 1
                                        ? "Shared by 1 person"
                                        : "Shared by \(expense.participantCount) people")
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                }
                                .padding(.vertical, 4)
                            }
                        }
                    }
                }
            }
        }
        .listStyle(.insetGrouped)
        .navigationTitle(state.tripName)
        .navigationBarTitleDisplayMode(.inline)
        .overlay {
            if isLoading {
                ProgressView("Loading trip…")
            } else if let errorMessage {
                ContentUnavailableView {
                    Label("Unable to load trip", systemImage: "exclamationmark.triangle")
                } description: {
                    Text(errorMessage)
                } actions: {
                    Button("Try again", action: onRetry)
                }
            }
        }
    }
}

private let overviewPreview = TripOverviewState(
    tripName: "Lisbon weekend",
    participantNames: "You, John, Dean",
    totalExpenses: "120,00 €",
    balanceText: "You are owed 50,00 €",
    balanceCents: 5000,
    expenses: [
        ExpenseRow(id: "dinner", description: "Dinner", amount: "90,00 €", paidBy: "You", participantCount: 3),
        ExpenseRow(id: "taxi", description: "Taxi", amount: "30,00 €", paidBy: "John", participantCount: 3)
    ]
)

#Preview("Overview") {
    NavigationStack { TripOverviewContent(state: overviewPreview) }
}

#Preview("Overview — Dark") {
    NavigationStack { TripOverviewContent(state: overviewPreview) }
        .preferredColorScheme(.dark)
}

#Preview("No expenses") {
    NavigationStack {
        TripOverviewContent(state: TripOverviewState(
            tripName: "Berlin", participantNames: "You, John", totalExpenses: "0,00 €", balanceText: "Settled up"
        ))
    }
}

#Preview("Loading") {
    NavigationStack { TripOverviewContent(state: TripOverviewState(), isLoading: true) }
}

#Preview("Error") {
    NavigationStack {
        TripOverviewContent(state: TripOverviewState(), errorMessage: "Could not load this trip.")
    }
}
