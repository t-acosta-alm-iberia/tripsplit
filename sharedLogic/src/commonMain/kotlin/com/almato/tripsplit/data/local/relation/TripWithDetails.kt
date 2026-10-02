package com.almato.tripsplit.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.almato.tripsplit.data.local.entity.ExpenseEntity
import com.almato.tripsplit.data.local.entity.ParticipantEntity
import com.almato.tripsplit.data.local.entity.TripEntity

data class TripWithDetails(
    @Embedded val trip: TripEntity,
    @Relation(parentColumn = "id", entityColumn = "tripId")
    val participants: List<ParticipantEntity>,
    @Relation(
        entity = ExpenseEntity::class,
        parentColumn = "id",
        entityColumn = "tripId",
    )
    val expenses: List<ExpenseWithParticipants>,
)
