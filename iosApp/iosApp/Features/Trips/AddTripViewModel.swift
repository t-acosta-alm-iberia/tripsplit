import Foundation
import Observation
import SharedLogic

@MainActor
@Observable
final class AddTripViewModel {
    private let repository: any TripRepository

    var name = ""
    var participantNames = ""
    private(set) var isSaving = false
    private(set) var errorMessage: String?

    var canSave: Bool {
        !name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty && !isSaving
    }

    init(repository: any TripRepository) {
        self.repository = repository
    }

    func save() async -> Bool {
        guard !isSaving else { return false }
        let tripName = name.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !tripName.isEmpty else {
            errorMessage = "Enter a trip name."
            return false
        }

        let id = UUID().uuidString
        let otherParticipants = participantNames
            .components(separatedBy: .newlines)
            .map { $0.trimmingCharacters(in: .whitespacesAndNewlines) }
            .filter { !$0.isEmpty }
            .map { Participant(id: UUID().uuidString, name: $0) }
        let participants = [Participant(id: "\(id):you", name: "You")] + otherParticipants

        isSaving = true
        errorMessage = nil
        defer { isSaving = false }

        do {
            try await repository.createTrip(id: id, name: tripName, participants: participants)
            return true
        } catch {
            errorMessage = "Could not save the trip. Please try again."
            return false
        }
    }
}
