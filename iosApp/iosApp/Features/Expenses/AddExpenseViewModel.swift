import Foundation
import Observation
import SharedLogic

@MainActor
@Observable
final class AddExpenseViewModel {
    private let repository: any TripRepository
    private let validateExpense: ValidateExpense
    private let addExpense: AddExpense
    private let tripId: String

    var amountText = ""
    var description = ""
    var paidByParticipantId = ""
    var selectedParticipantIds: Set<String> = []
    private(set) var participants: [Participant] = []
    private(set) var isLoading = true
    private(set) var isSaving = false
    private(set) var loadError: String?
    private(set) var saveError: String?

    init(tripId: String, repository: any TripRepository, validateExpense: ValidateExpense = ValidateExpense()) {
        self.tripId = tripId
        self.repository = repository
        self.validateExpense = validateExpense
        self.addExpense = AddExpense(repository: repository, validateExpense: validateExpense)
    }

    private var parsedAmountCents: KotlinLong? {
        AmountParserKt.parseAmountCents(text: amountText)
    }

    var validationError: ExpenseValidationError? {
        validateExpense.invoke(
            description: description,
            amountCents: parsedAmountCents,
            paidByParticipantId: paidByParticipantId,
            splitParticipantIds: selectedParticipantIds,
            tripParticipantIds: Set(participants.map(\.id))
        )
    }

    // Shown only after the user starts filling in the form, so an empty form opens without an error.
    var validationMessage: String? {
        guard !amountText.isEmpty || !description.isEmpty else { return nil }
        return validationError?.message
    }

    var canSave: Bool {
        !isLoading && !isSaving && loadError == nil && validationError == nil
    }

    func load() async {
        isLoading = true
        loadError = nil
        defer { isLoading = false }
        do {
            guard let trip = try await repository.getTrip(id: tripId) else {
                loadError = "This trip could not be found."
                return
            }
            participants = trip.participants
            selectedParticipantIds = Set(participants.map(\.id))
            paidByParticipantId = participants.first { $0.id == "\(tripId):you" }?.id
                ?? participants.first?.id ?? ""
        } catch {
            loadError = "Could not load the participants. Please try again."
        }
    }

    func save() async -> Bool {
        guard canSave else { return false }
        isSaving = true
        saveError = nil
        defer { isSaving = false }
        do {
            // Validates again against the stored trip, independently of the Save button state.
            if let error = try await addExpense.invoke(
                tripId: tripId,
                description: description,
                amountCents: parsedAmountCents,
                paidByParticipantId: paidByParticipantId,
                splitParticipantIds: selectedParticipantIds
            ) {
                saveError = error.message
                return false
            }
            return true
        } catch {
            saveError = "Could not save the expense. Please try again."
            return false
        }
    }
}

private extension ExpenseValidationError {
    var message: String {
        switch self {
        case .blankDescription: "Enter a description."
        case .invalidAmount, .nonPositiveAmount: "Enter a positive amount with up to two decimal places."
        case .unknownPayer: "Select who paid."
        case .noParticipants: "Select at least one person to share the expense."
        case .unknownParticipant: "Check the selected participants."
        default: "Check the expense details."
        }
    }
}
