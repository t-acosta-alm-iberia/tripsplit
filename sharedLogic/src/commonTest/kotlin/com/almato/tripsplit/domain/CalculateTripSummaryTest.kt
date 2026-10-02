package com.almato.tripsplit.domain

import com.almato.tripsplit.domain.model.Expense
import com.almato.tripsplit.domain.model.Participant
import com.almato.tripsplit.domain.model.Trip
import com.almato.tripsplit.usecase.CalculateTripSummary
import com.almato.tripsplit.usecase.TripSummary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CalculateTripSummaryTest {
    private val calculateTripSummary = CalculateTripSummary()
    private val participants = listOf("1", "2", "3").map { Participant(it, it) }

    @Test
    fun distributesRemainderByIdAndConservesMoney() {
        val trip = trip(expense(1000, split = listOf("3", "1", "2")))
        val summaries = participants.map { calculateTripSummary(trip, it.id) }
        assertEquals(listOf(334L, 333L, 333L), summaries.map { it.shareCents })
        assertEquals(1000L, summaries.sumOf { it.shareCents })
        assertEquals(0L, summaries.sumOf { it.balanceCents })
    }

    @Test
    fun payerCanBeExcludedFromSplit() {
        val trip = trip(expense(9000, split = listOf("2", "3")))
        assertEquals(9000L, calculateTripSummary(trip, "1").balanceCents)
        assertEquals(-4500L, calculateTripSummary(trip, "2").balanceCents)
    }

    @Test
    fun totalsMultipleExpenses() {
        val summary = calculateTripSummary(trip(expense(9000), expense(3000, payer = "2")), "1")
        assertEquals(TripSummary(12000, 4000, 9000), summary)
        assertEquals(5000L, summary.balanceCents)
    }

    @Test
    fun emptyTripIsSettled() {
        assertEquals(TripSummary(0, 0, 0), calculateTripSummary(trip(), "1"))
    }

    @Test
    fun rejectsInvalidSplitsAndReferences() {
        for (invalid in listOf(
            expense(100, split = emptyList()),
            expense(100, split = listOf("1", "1")),
            expense(100, split = listOf("missing")),
            expense(100, payer = "missing"),
            expense(0),
        )) {
            assertFailsWith<IllegalArgumentException> { calculateTripSummary(trip(invalid), "1") }
        }
        assertFailsWith<IllegalArgumentException> { calculateTripSummary(trip(), "missing") }
    }

    @Test
    fun rejectsOverflowingTotal() {
        assertFailsWith<IllegalArgumentException> {
            calculateTripSummary(trip(expense(Long.MAX_VALUE), expense(1)), "1")
        }
    }

    private fun trip(vararg expenses: Expense) = Trip("lisbon", "Lisbon", participants, expenses.toList())
    private fun expense(
        amount: Long,
        payer: String = "1",
        split: List<String> = listOf("1", "2", "3"),
    ) = Expense("expense", "Dinner", amount, payer, split)
}
