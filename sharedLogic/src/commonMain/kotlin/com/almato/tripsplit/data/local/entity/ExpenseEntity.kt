package com.almato.tripsplit.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "expenses", indices = [Index("tripId")])
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val description: String,
    val amountCents: Long,
    val paidByParticipantId: String,
)
