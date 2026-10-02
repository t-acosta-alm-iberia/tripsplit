package com.almato.tripsplit.domain.repository

import com.almato.tripsplit.domain.model.Participant

import com.almato.tripsplit.domain.model.Expense
import com.almato.tripsplit.domain.model.TripListItem
import com.almato.tripsplit.domain.model.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface TripRepository {
    // A snapshot for native callers that do not yet bridge Flow.
    @Throws(Exception::class)
    suspend fun getTrips(): List<TripListItem> = observeTrips().first()

    fun observeTrips(): Flow<List<TripListItem>>

    @Throws(Exception::class)
    suspend fun getTrip(id: String): Trip? = observeTrip(id).first()

    fun observeTrip(id: String): Flow<Trip?>

    @Throws(Exception::class)
    suspend fun createTrip(id: String, name: String, participants: List<Participant>)

    @Throws(Exception::class)
    suspend fun addExpense(tripId: String, expense: Expense)
}