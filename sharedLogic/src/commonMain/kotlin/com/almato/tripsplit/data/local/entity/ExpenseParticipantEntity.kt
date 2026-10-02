package com.almato.tripsplit.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "expense_participants",
    primaryKeys = ["expenseId", "participantId"],
    indices = [Index("participantId")],
)
data class ExpenseParticipantEntity(
    val expenseId: String,
    val participantId: String,
)
