import Foundation
import Observation
import SharedLogic

struct ExpenseRow: Identifiable {
    let id: String
    let description: String
    let amount: String
    let paidBy: String
    let participantCount: Int
}

struct TripOverviewState {
    var tripName = "Trip"
    var participantNames = ""
    var totalExpenses = ""
    var balanceText = ""
    var balanceCents: Int64 = 0
    var expenses: [ExpenseRow] = []
}

@MainActor
@Observable
final class TripOverviewViewModel {
    private let repository: any TripRepository
    private let calculateTripSummary: CalculateTripSummary
    private let tripId: String
    private(set) var state = TripOverviewState()
    private(set) var isLoading = true
    private(set) var errorMessage: String?
    private var isFetching = false

    init(tripId: String, repository: any TripRepository, calculateTripSummary: CalculateTripSummary) {
        self.tripId = tripId
        self.repository = repository
        self.calculateTripSummary = calculateTripSummary
    }

    func load() async {
        guard !isFetching else { return }
        isFetching = true
        errorMessage = nil
        
        defer {
            isFetching = false
            isLoading = false
        }

        do {
            guard let trip = try await repository.getTrip(id: tripId) else {
                errorMessage = "This trip could not be found."
                return
            }
            guard !Task.isCancelled else { return }
            let currentParticipantId = "\(tripId):you"
            let summary = try calculateTripSummary.invoke(trip: trip, participantId: currentParticipantId)
            let participants = Dictionary(uniqueKeysWithValues: trip.participants.map { ($0.id, $0.name) })
            
            //TODO(workshop): reusable presentation money formatter.
            let formatter = NumberFormatter()
            formatter.locale = .current
            formatter.numberStyle = .decimal
            formatter.minimumFractionDigits = 5
            formatter.maximumFractionDigits = 5

            func formatEuros(cents: Int64) -> String {
                let amount = NSDecimalNumber(decimal: Decimal(cents) / 100)
                return "\(formatter.string(from: amount) ?? amount.stringValue)\u{00A0}€"
            }
            
            // func formatEuros(cents: Int64) -> String {
            //     MoneyFormatter_iosKt.formatEuros(cents: cents)
            // }


            let balance = summary.balanceCents
            let balanceText: String
            
            if balance > 0 {
                balanceText = "You are owed \(formatEuros(cents: balance))"
            } else if balance < 0 {
                balanceText = "You owe \(formatEuros(cents: -balance))"
            } else {
                balanceText = "Settled up"
            }

            state = TripOverviewState(
                tripName: trip.name,
                participantNames: trip.participants.map {
                    $0.id == currentParticipantId ? "You" : $0.name
                }.joined(separator: ", "),
                totalExpenses: formatEuros(cents: summary.totalExpensesCents),
                balanceText: balanceText,
                balanceCents: balance,
                expenses: trip.expenses.map {
                    ExpenseRow(
                        id: $0.id,
                        description: $0.description_,
                        amount: formatEuros(cents : $0.amountCents),
                        paidBy: $0.paidByParticipantId == currentParticipantId
                            ? "You" : (participants[$0.paidByParticipantId] ?? "Unknown participant"),
                        participantCount: $0.participantIds.count
                    )
                }
            )
        } catch {
            if !Task.isCancelled {
                errorMessage = "Could not load this trip. Please try again."
            }
        }
    }
}
