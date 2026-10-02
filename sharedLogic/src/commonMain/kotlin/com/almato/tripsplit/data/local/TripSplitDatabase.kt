package com.almato.tripsplit.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.almato.tripsplit.data.local.dao.ExpenseDao
import com.almato.tripsplit.data.local.dao.TripDao
import com.almato.tripsplit.data.local.entity.ExpenseEntity
import com.almato.tripsplit.data.local.entity.ExpenseParticipantEntity
import com.almato.tripsplit.data.local.entity.ParticipantEntity
import com.almato.tripsplit.data.local.entity.TripEntity

@Database(
    entities = [
        TripEntity::class,
        ParticipantEntity::class,
        ExpenseEntity::class,
        ExpenseParticipantEntity::class
    ],
    version = 1
)
@ConstructedBy(TripSplitDatabaseConstructor::class)
abstract class TripSplitDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao

    abstract fun expenseDao(): ExpenseDao
}

@Suppress("KotlinNoActualForExpect")
expect object TripSplitDatabaseConstructor : RoomDatabaseConstructor<TripSplitDatabase> {
    override fun initialize(): TripSplitDatabase
}