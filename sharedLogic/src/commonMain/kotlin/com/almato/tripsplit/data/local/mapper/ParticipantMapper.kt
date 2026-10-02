package com.almato.tripsplit.data.local.mapper

import com.almato.tripsplit.data.local.entity.ParticipantEntity
import com.almato.tripsplit.domain.model.Participant

fun ParticipantEntity.toDomain(): Participant {
    return Participant(
        id = id,
        name = name
    )
}
