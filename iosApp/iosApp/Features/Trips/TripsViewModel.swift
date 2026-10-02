import Observation
import SharedLogic

@MainActor
@Observable
final class TripsViewModel {
    private let repository: any TripRepository
    private(set) var trips: [TripListItem] = []
    private(set) var isLoading = false
    private(set) var errorMessage: String?

    init(repository: any TripRepository) {
        self.repository = repository
    }

    func loadTrips() async {
        guard !isLoading else { return }
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }
        do {
            trips = try await repository.getTrips()
        } catch {
            errorMessage = "Could not load trips. Please try again."
        }
    }
}
