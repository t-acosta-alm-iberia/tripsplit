import SwiftUI
import SharedLogic

enum Destination: Hashable {
    case overview(tripId: String)
}

struct AppRootView: View {
    private let repository: any TripRepository
    @State private var viewModel: TripsViewModel
    @State private var showingAddTrip = false
    @State private var path: [Destination] = []
    private let calculateTripSummary: CalculateTripSummary
    private let appPreferences: any AppPreferences
    private let overviewPreferences: OverviewPreferences

    init() {
        let dependencies = IosDependencies()
        let repository = dependencies.tripRepository
        self.calculateTripSummary = dependencies.calculateTripSummary
        self.appPreferences = dependencies.appPreferences
        self.overviewPreferences = dependencies.overviewPreferences
        self.repository = repository
        _viewModel = State(initialValue: TripsViewModel(repository: repository))
    }

    var body: some View {
        NavigationStack(path: $path) {
            TripsView(
                trips: viewModel.trips,
                onTripSelected: { tripId in
                    path.append(.overview(tripId: tripId))
                },
                onAddTrip: { showingAddTrip = true },
                isLoading: viewModel.isLoading,
                errorMessage: viewModel.errorMessage,
                onRetry: { Task { await viewModel.loadTrips() } }
            )
            .navigationDestination(for: Destination.self) { destination in
                switch destination {
                case .overview(let tripId):
                    TripOverviewView(
                        tripId: tripId,
                        repository: repository,
                        calculateTripSummary: calculateTripSummary,
                        appPreferences: appPreferences,
                        overviewPreferences: overviewPreferences
                    )
                }
            }
        }
        .task { await viewModel.loadTrips() }
        .sheet(isPresented: $showingAddTrip) {
            AddTripView(repository: repository) {
                Task { await viewModel.loadTrips() }
            }
        }
    }
}
