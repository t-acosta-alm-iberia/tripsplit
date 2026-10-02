package com.almato.tripsplit.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.almato.tripsplit.data.local.entity.ExpenseEntity
import com.almato.tripsplit.data.local.entity.ExpenseParticipantEntity

data class ExpenseWithParticipants(
    @Embedded val expense: ExpenseEntity,
    @Relation(parentColumn = "id", entityColumn = "expenseId")
    val participants: List<ExpenseParticipantEntity>,
)
