import SwiftUI
import SharedLogic

struct AddExpenseView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var viewModel: AddExpenseViewModel
    let onSaved: () -> Void

    init(tripId: String, repository: any TripRepository, onSaved: @escaping () -> Void) {
        _viewModel = State(initialValue: AddExpenseViewModel(tripId: tripId, repository: repository))
        self.onSaved = onSaved
    }

    var body: some View {
        NavigationStack {
            Group {
                if viewModel.isLoading {
                    ProgressView("Loading participants…")
                } else if let error = viewModel.loadError {
                    ContentUnavailableView {
                        Label("Unable to load trip", systemImage: "exclamationmark.triangle")
                    } description: {
                        Text(error)
                    } actions: {
                        Button("Try again") { Task { await viewModel.load() } }
                    }
                } else {
                    AddExpenseForm(
                        amountText: $viewModel.amountText,
                        description: $viewModel.description,
                        paidByParticipantId: $viewModel.paidByParticipantId,
                        selectedParticipantIds: $viewModel.selectedParticipantIds,
                        participants: viewModel.participants,
                        errorMessage: viewModel.saveError ?? viewModel.validationMessage
                    )
                    .disabled(viewModel.isSaving)
                }
            }
            .navigationTitle("Add expense")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                        .disabled(viewModel.isSaving)
                }
                ToolbarItem(placement: .confirmationAction) {
                    if viewModel.isSaving {
                        ProgressView().accessibilityLabel("Saving expense")
                    } else {
                        Button("Save") {
                            Task {
                                if await viewModel.save() {
                                    onSaved()
                                    dismiss()
                                }
                            }
                        }
                        .disabled(!viewModel.canSave)
                    }
                }
            }
        }
        .task { await viewModel.load() }
        .interactiveDismissDisabled(viewModel.isSaving)
    }
}

private struct AddExpenseForm: View {
    @Binding var amountText: String
    @Binding var description: String
    @Binding var paidByParticipantId: String
    @Binding var selectedParticipantIds: Set<String>
    let participants: [Participant]
    let errorMessage: String?

    var body: some View {
        Form {
            Section("Expense") {
                HStack {
                    TextField("Amount", text: $amountText)
                        .keyboardType(.decimalPad)
                        .accessibilityLabel("Amount in euros")
                    Text("€").foregroundStyle(.secondary)
                }
                TextField("Description, e.g. dinner", text: $description)
                    .textInputAutocapitalization(.sentences)
            }
            Section {
                Picker("Paid by", selection: $paidByParticipantId) {
                    ForEach(participants, id: \.id) { participant in
                        Text(participant.name).tag(participant.id)
                    }
                }
            }
            Section {
                ForEach(participants, id: \.id) { participant in
                    Toggle(participant.name, isOn: Binding(
                        get: { selectedParticipantIds.contains(participant.id) },
                        set: { selected in
                            if selected { selectedParticipantIds.insert(participant.id) }
                            else { selectedParticipantIds.remove(participant.id) }
                        }
                    ))
                }
            } header: {
                Text("Split equally between")
            } footer: {
                Text(selectedParticipantIds.isEmpty
                    ? "Select at least one participant."
                    : "The expense will be shared equally between the selected participants. Any remaining cents are distributed one at a time.")
            }
            if let errorMessage {
                Section { Text(errorMessage).foregroundStyle(.red) }
            }
        }
    }
}

#Preview("Add expense") {
    NavigationStack {
        AddExpenseForm(
            amountText: .constant("45,50"),
            description: .constant("Dinner"),
            paidByParticipantId: .constant("you"),
            selectedParticipantIds: .constant(["you", "john", "dean"]),
            participants: [
                Participant(id: "you", name: "You"),
                Participant(id: "john", name: "John"),
                Participant(id: "dean", name: "Dean")
            ],
            errorMessage: nil
        )
        .navigationTitle("Add expense")
        .navigationBarTitleDisplayMode(.inline)
    }
}
