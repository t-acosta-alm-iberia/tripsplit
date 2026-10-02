package com.almato.tripsplit.domain

import com.almato.tripsplit.domain.model.Expense
import com.almato.tripsplit.domain.model.Participant
import com.almato.tripsplit.domain.model.Trip
import com.almato.tripsplit.domain.model.TripListItem
import com.almato.tripsplit.domain.repository.TripRepository
import com.almato.tripsplit.usecase.AddExpense
import com.almato.tripsplit.usecase.ExpenseValidationError
import com.almato.tripsplit.usecase.ValidateExpense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AddExpenseTest {
    private val trip = Trip("lisbon", "Lisbon", listOf("1", "2", "3").map { Participant(it, it) }, emptyList())
    private val repository = RecordingTripRepository(trip)
    private val addExpense = AddExpense(repository, ValidateExpense())

    @Test fun savesValidExpenseWithTrimmedDescriptionAndSortedSplit() = runTest {
        assertNull(addExpense("lisbon", " Dinner ", 1000, "1", setOf("3", "1")))
        val saved = repository.saved.single()
        assertEquals("lisbon", saved.first)
        assertEquals("Dinner", saved.second.description)
        assertEquals(listOf("1", "3"), saved.second.participantIds)
    }

    @Test fun invalidExpenseIsNeverSaved() = runTest {
        assertEquals(ExpenseValidationError.BLANK_DESCRIPTION, addExpense("lisbon", " ", 1000, "1", setOf("1")))
        assertEquals(ExpenseValidationError.UNKNOWN_PARTICIPANT, addExpense("lisbon", "Dinner", 1000, "1", setOf("missing")))
        assertTrue(repository.saved.isEmpty())
    }

    @Test fun unknownTripFails() = runTest {
        assertFailsWith<IllegalArgumentException> { addExpense("missing", "Dinner", 1000, "1", setOf("1")) }
        assertTrue(repository.saved.isEmpty())
    }
}

private class RecordingTripRepository(private val trip: Trip) : TripRepository {
    val saved = mutableListOf<Pair<String, Expense>>()

    override fun observeTrips(): Flow<List<TripListItem>> = flowOf(listOf(TripListItem(trip.id, trip.name)))
    override fun observeTrip(id: String): Flow<Trip?> = flowOf(trip.takeIf { it.id == id })
    override suspend fun createTrip(id: String, name: String, participants: List<Participant>) = error("Not used")
    override suspend fun addExpense(tripId: String, expense: Expense) { saved += tripId to expense }
}
