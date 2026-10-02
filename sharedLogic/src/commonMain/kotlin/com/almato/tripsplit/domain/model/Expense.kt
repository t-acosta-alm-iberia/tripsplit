package com.almato.tripsplit.domain.model

data class Expense(
    val id: String,
    val description: String,
    val amountCents: Long,
    val paidByParticipantId: String,
    val participantIds: List<String>
)
