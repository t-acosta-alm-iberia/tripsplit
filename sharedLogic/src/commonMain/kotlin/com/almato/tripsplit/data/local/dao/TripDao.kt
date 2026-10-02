package com.almato.tripsplit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.almato.tripsplit.data.local.entity.ParticipantEntity
import com.almato.tripsplit.data.local.entity.TripEntity
import com.almato.tripsplit.data.local.relation.ExpenseWithParticipants
import com.almato.tripsplit.data.local.relation.TripWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY name COLLATE NOCASE, id")
    fun observeTrips(): Flow<List<TripEntity>>

    @Transaction
    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun observeTrip(tripId: String): Flow<TripWithDetails?>

    @Insert
    suspend fun insertTrip(trip: TripEntity)

    @Insert
    suspend fun insertParticipants(participants: List<ParticipantEntity>)

    @Transaction
    suspend fun insertTripWithParticipants(
        trip: TripEntity,
        participants: List<ParticipantEntity>
    ) {
        insertTrip(trip)
        insertParticipants(participants)
    }
}