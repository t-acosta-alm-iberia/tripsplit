package com.almato.tripsplit.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "participants", indices = [Index("tripId")])
data class ParticipantEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val name: String,
)
