package com.almato.tripsplit.domain.model

data class Trip(
    val id: String,
    val name: String,
    val participants: List<Participant>,
    val expenses: List<Expense>
)
