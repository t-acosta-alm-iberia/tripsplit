package com.almato.tripsplit.data.local.mapper

import com.almato.tripsplit.data.local.relation.TripWithDetails
import com.almato.tripsplit.domain.model.Trip

fun TripWithDetails.toDomain(): Trip {
    return Trip(
        id = trip.id,
        name = trip.name,
        participants = participants.map { it.toDomain() },
        expenses = expenses.map { it.toDomain() }
    )
}
