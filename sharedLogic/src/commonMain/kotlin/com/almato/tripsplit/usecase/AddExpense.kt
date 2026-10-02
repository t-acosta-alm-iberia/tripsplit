package com.almato.tripsplit.usecase

import com.almato.tripsplit.domain.model.Expense
import com.almato.tripsplit.domain.repository.TripRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddExpense(
    private val repository: TripRepository,
    private val validateExpense: ValidateExpense,
) {
    @OptIn(ExperimentalUuidApi::class)
    @Throws(Exception::class)
    suspend operator fun invoke(
        tripId: String,
        description: String,
        amountCents: Long?,
        paidByParticipantId: String?,
        splitParticipantIds: Set<String>,
    ): ExpenseValidationError? {
        val trip = requireNotNull(repository.getTrip(tripId)) { "Trip not found" }

        validateExpense(
            description = description,
            amountCents = amountCents,
            paidByParticipantId = paidByParticipantId,
            splitParticipantIds = splitParticipantIds,
            tripParticipantIds = trip.participants.map { it.id }.toSet(),
        )?.let { return it }

        repository.addExpense(
            tripId,
            Expense(
                id = Uuid.random().toString(),
                description = description.trim(),
                amountCents = requireNotNull(amountCents),
                paidByParticipantId = requireNotNull(paidByParticipantId),
                participantIds = splitParticipantIds.sorted(),
            ),
        )

        return null
    }
}
