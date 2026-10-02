package com.almato.tripsplit.usecase

import com.almato.tripsplit.domain.model.Trip
import com.almato.tripsplit.domain.splitEvenly

data class TripSummary(
    val totalExpensesCents: Long, // total cost of the trip
    val shareCents: Long, // Part of the trip's spending that was for this participant.
    val paidCents: Long, // What this participant paid.
) {
    // Positive means the participant is owed money; negative means they owe money.
    val balanceCents: Long get() = paidCents - shareCents
}

class CalculateTripSummary {
    @Throws(IllegalArgumentException::class)
    operator fun invoke(trip: Trip, participantId: String): TripSummary {
        val tripParticipantIds = trip.participants.map { it.id }.toSet()
        require(tripParticipantIds.size == trip.participants.size) { "Participant IDs must be unique" }
        require(participantId in tripParticipantIds) { "Participant does not belong to this trip" }

        var totalCents = 0L
        var shareCents = 0L
        var paidCents = 0L

        for (expense in trip.expenses) {
            require(expense.amountCents > 0) { "Expense amount must be positive" }
            require(expense.paidByParticipantId in tripParticipantIds) { "Unknown payer" }
            require(expense.participantIds.isNotEmpty()) { "Select at least one participant" }
            require(expense.participantIds.distinct().size == expense.participantIds.size) {
                "An expense cannot include a participant twice"
            }
            require(expense.participantIds.all { it in tripParticipantIds }) { "Unknown split participant" }
            require(expense.amountCents <= Long.MAX_VALUE - totalCents) { "Trip total is too large" }

            totalCents += expense.amountCents
            if (expense.paidByParticipantId == participantId) paidCents += expense.amountCents

            splitEvenly(expense.amountCents, expense.participantIds)[participantId]?.let { shareCents += it }
        }

        return TripSummary(totalCents, shareCents, paidCents)
    }
}