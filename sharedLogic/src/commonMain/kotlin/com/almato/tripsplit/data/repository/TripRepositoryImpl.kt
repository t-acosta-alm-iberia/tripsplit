package com.almato.tripsplit.data.repository

import com.almato.tripsplit.data.local.entity.ParticipantEntity

import com.almato.tripsplit.data.local.entity.TripEntity

import com.almato.tripsplit.domain.model.Participant

import com.almato.tripsplit.data.local.dao.ExpenseDao
import com.almato.tripsplit.data.local.dao.TripDao
import com.almato.tripsplit.data.local.mapper.toDomain
import com.almato.tripsplit.data.local.mapper.toEntity
import com.almato.tripsplit.data.local.mapper.toParticipantLinks
import com.almato.tripsplit.domain.model.Expense
import com.almato.tripsplit.domain.model.TripListItem
import com.almato.tripsplit.domain.model.Trip
import com.almato.tripsplit.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TripRepositoryImpl(
    private val expenseDao: ExpenseDao,
    private val tripDao: TripDao
) : TripRepository {

    override fun observeTrips(): Flow<List<TripListItem>> =
        tripDao.observeTrips().map { trips ->
            trips.map { TripListItem(id = it.id, name = it.name) }
        }

    override fun observeTrip(id: String): Flow<Trip?> {
        return tripDao.observeTrip(id).map { tripWithDetails ->
            tripWithDetails?.toDomain()
        }
    }

    override suspend fun createTrip(
        id: String,
        name: String,
        participants: List<Participant>,
    ) {
        require(name.isNotBlank())
        require(participants.isNotEmpty())
        require(participants.all { it.name.isNotBlank() })
        require(participants.map { it.id }.distinct().size == participants.size)

        tripDao.insertTripWithParticipants(
            trip = TripEntity(id, name.trim()),
            participants = participants.map {
                ParticipantEntity(it.id, id, it.name.trim())
            },
        )
    }

    override suspend fun addExpense(tripId: String, expense: Expense) {
        val entity = expense.toEntity(tripId)
        val links = expense.toParticipantLinks()

        expenseDao.insertExpensesWithParticipants(
            expense = entity,
            links = links,
        )
    }
}