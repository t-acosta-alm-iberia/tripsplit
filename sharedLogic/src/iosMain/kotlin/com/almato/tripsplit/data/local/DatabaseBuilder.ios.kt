package com.almato.tripsplit.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun createDatabaseBuilder(): RoomDatabase.Builder<TripSplitDatabase> {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )

    val directoryPath = requireNotNull(directory?.path)

    return Room.databaseBuilder<TripSplitDatabase>(
        name = "$directoryPath/tripsplit.db"
    )
}