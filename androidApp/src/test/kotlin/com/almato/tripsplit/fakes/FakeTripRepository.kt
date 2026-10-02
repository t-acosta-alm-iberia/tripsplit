package com.almato.tripsplit.fakes

import com.almato.tripsplit.domain.model.Expense
import com.almato.tripsplit.domain.model.Participant
import com.almato.tripsplit.domain.model.Trip
import com.almato.tripsplit.domain.model.TripListItem
import com.almato.tripsplit.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeTripRepository(initialTrips: List<Trip> = emptyList()) : TripRepository {

    private val trips = MutableStateFlow(initialTrips.associateBy { it.id })

    override fun observeTrips(): Flow<List<TripListItem>> {
        return trips.map { all -> all.values.map { TripListItem(it.id, it.name) } }
    }

    override fun observeTrip(id: String): Flow<Trip?> {
        return trips.map { it[id] }
    }

    override suspend fun createTrip(
        id: String,
        name: String,
        participants: List<Participant>
    ) {
        trips.update { it + (id to Trip(id, name, participants, emptyList())) }
    }

    override suspend fun addExpense(
        tripId: String,
        expense: Expense
    ) {
        trips.update { all ->
            val trip = all.getValue(tripId)
            all + (tripId to trip.copy(expenses = trip.expenses + expense))
        }
    }

}