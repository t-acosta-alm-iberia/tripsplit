package com.almato.tripsplit.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun createDatabaseBuilder(
    context: Context
): RoomDatabase.Builder<TripSplitDatabase> {
    val appContext = context.applicationContext
    val databaseFile = appContext.getDatabasePath("tripsplit.db")
    return Room.databaseBuilder<TripSplitDatabase>(
        appContext,
        name = databaseFile.absolutePath
    )
}