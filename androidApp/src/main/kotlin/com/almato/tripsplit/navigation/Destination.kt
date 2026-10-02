package com.almato.tripsplit.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Destination: NavKey {
    @Serializable
    data object Trips : Destination

    @Serializable
    data object AddTrip : Destination

    @Serializable
    data class Overview(val tripId: String) : Destination

    @Serializable
    data class AddExpense(val tripId: String) : Destination
}