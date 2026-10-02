package com.almato.tripsplit.data.local.mapper

import com.almato.tripsplit.data.local.entity.ExpenseEntity
import com.almato.tripsplit.data.local.entity.ExpenseParticipantEntity
import com.almato.tripsplit.data.local.relation.ExpenseWithParticipants
import com.almato.tripsplit.domain.model.Expense

fun Expense.toEntity(tripId: String): ExpenseEntity =
    ExpenseEntity(
        id = id,
        tripId = tripId,
        description = description,
        amountCents = amountCents,
        paidByParticipantId = paidByParticipantId,
    )

fun Expense.toParticipantLinks(): List<ExpenseParticipantEntity> =
    participantIds.map { participantId ->
        ExpenseParticipantEntity(
            expenseId = id,
            participantId = participantId,
        )
    }

fun ExpenseWithParticipants.toDomain(): Expense =
    Expense(
        id = expense.id,
        description = expense.description,
        amountCents = expense.amountCents,
        paidByParticipantId = expense.paidByParticipantId,
        participantIds = participants.map { it.participantId },
    )
