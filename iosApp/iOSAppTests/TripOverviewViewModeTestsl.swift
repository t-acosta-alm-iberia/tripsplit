//
//  TripOverviewViewModel.swift
//  iosApp
//
//  Created by tim.acosta on 01/10/2026.
//

import Testing
import SharedLogic
@testable import TripSplit

@MainActor
struct TripOverviewViewModelTests {
    private let repository = FakeTripRepository()
    
    init() async throws {
        try await repository.createTrip(
            id: "trip-1",
            name: "Lisbon",
            participants: [
                Participant(id: "trip-1:you", name: "You"),
                Participant(id: "user-2", name: "Ana"),
                Participant(id: "user-3", name: "Marc"),
            ]
        )
        
        // Paid by you, split equally between all three: 30,00 € each.
        try await repository.addExpense(tripId: "trip-1", expense: Expense(
            id: "expense-1", description: "Dinner", amountCents: 9_000,
            paidByParticipantId: "trip-1:you", participantIds: ["trip-1:you", "user-2", "user-3"]))
        // Paid by Ana, split between you and Ana: 12,50 € each.
        try await repository.addExpense(tripId: "trip-1", expense: Expense(
            id: "expense-2", description: "Taxi", amountCents: 2_500,
            paidByParticipantId: "user-2", participantIds: ["trip-1:you", "user-2"]))
        // Paid by Marc, 10,00 € between three: 3,34 € for you (first sorted ID), 3,33 € for the others.
        try await repository.addExpense(tripId: "trip-1", expense: Expense(
            id: "expense-3", description: "Museum", amountCents: 1_000,
            paidByParticipantId: "user-3", participantIds: ["trip-1:you", "user-2", "user-3"]))
    }
    
    @Test func balanceTextShowsAmountOwedWhenYouPaidMoreThanYourShare() async {
        let viewModel = TripOverviewViewModel(
            tripId: "trip-1",
            repository: repository,
            calculateTripSummary: CalculateTripSummary()
        )
        
        await viewModel.load()
        
        #expect(viewModel.state.balanceText == "You are owed 44,16\u{00A0}€")
    }
}

