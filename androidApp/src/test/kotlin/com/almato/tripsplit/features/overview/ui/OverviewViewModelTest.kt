package com.almato.tripsplit.features.overview.ui

import com.almato.tripsplit.usecase.CalculateTripSummary
import com.almato.tripsplit.domain.model.Expense
import com.almato.tripsplit.domain.model.Participant
import com.almato.tripsplit.domain.repository.TripRepository
import com.almato.tripsplit.fakes.FakeTripRepository
import com.almato.tripsplit.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class OverviewViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    val repository: TripRepository = FakeTripRepository()

    @Before
    fun setup() = runTest {
        Locale.setDefault(Locale.forLanguageTag("es-ES"))
        repository.createTrip(
            id = "trip-1",
            name = "Lisbon",
            participants = listOf(
                Participant(id = "user-1", name = "You"),
                Participant(id = "user-2", name = "Ana"),
                Participant(id = "user-3", name = "Marc"),
            ),
        )
        // Paid by you, split equally between all three: 30,00 € each.
        repository.addExpense(
            "trip-1",
            Expense(
                id = "expense-1",
                description = "Dinner",
                amountCents = 9_000,
                paidByParticipantId = "user-1",
                participantIds = listOf("user-1", "user-2", "user-3"),
            ),
        )
        // Paid by Ana, split between you and Ana: 12,50 € each.
        repository.addExpense(
            "trip-1",
            Expense(
                id = "expense-2",
                description = "Taxi",
                amountCents = 2_500,
                paidByParticipantId = "user-2",
                participantIds = listOf("user-1", "user-2"),
            ),
        )
        // Paid by Marc, 10,00 € between three: 3,34 € for you (first sorted ID), 3,33 € for the others.
        repository.addExpense(
            "trip-1",
            Expense(
                id = "expense-3",
                description = "Museum",
                amountCents = 1_000,
                paidByParticipantId = "user-3",
                participantIds = listOf("user-1", "user-2", "user-3"),
            ),
        )
    }


    @Test
    fun `balance text shows amount owed when you paid more than your share`() = runTest {
        val viewModel = buildViewModel()

        viewModel.loadTrip(
            tripId = "trip-1",
            currentParticipantId = "user-1"
        )

        assertEquals("You are owed 44,16\u00A0€", viewModel.state.value.balanceText)
    }

    private fun buildViewModel(
        repository: TripRepository = this.repository
    ): OverviewViewModel {
        return OverviewViewModel(
            repository = repository,
            calculateTripSummary = CalculateTripSummary()
        )
    }

}