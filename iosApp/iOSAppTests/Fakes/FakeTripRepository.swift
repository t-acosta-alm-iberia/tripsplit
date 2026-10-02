//
//  FakeTripRepository.swift
//  iosApp
//
//  Created by tim.acosta on 01/10/2026.
//

import SharedLogic

final class FakeTripRepository: NSObject, TripRepository {
    private var trips: [String: Trip]
    
    init(trips: [Trip] = []) {
        self.trips = Dictionary(uniqueKeysWithValues: trips.map {
            ($0.id, $0)
        })
    }
    
    func getTrip(id: String) async throws -> Trip? {
        trips[id]
    }
    
    func getTrips() async throws -> [TripListItem] {
        trips.values.map {
            TripListItem(id: $0.id, name: $0.name)
        }
    }
    
    func createTrip(id: String, name: String, participants: [Participant]) async throws {
        trips[id] = Trip(id: id, name: name, participants: participants, expenses: [])
    }
    
    func addExpense(tripId: String, expense: Expense) async throws {
        guard let trip = trips[tripId] else { fatalError("Unknown trip \(tripId)")}
        trips[tripId] = trip.doCopy(id: trip.id, name: trip.name, participants: trip.participants, expenses: trip.expenses + [expense])
    }
    
    // Swift can't easily create a Kotlin Flow, and the iOS ViewModels don't observe yet.
    func observeTrip(id: String) -> any Kotlinx_coroutines_coreFlow { fatalError("Not used on iOS") }
    func observeTrips() -> any Kotlinx_coroutines_coreFlow { fatalError("Not used on iOS") }
}
