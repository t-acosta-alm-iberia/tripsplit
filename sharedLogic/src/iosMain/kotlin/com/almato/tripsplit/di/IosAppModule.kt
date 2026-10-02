package com.almato.tripsplit.di

import com.almato.tripsplit.data.local.TripSplitDatabase
import com.almato.tripsplit.data.local.createDatabase
import com.almato.tripsplit.data.local.createDatabaseBuilder
import com.almato.tripsplit.data.local.preferences.AppPreferences
import com.almato.tripsplit.data.local.preferences.UserDefaultsAppPreferences
import org.koin.dsl.module

val iOSAppModule = module {
    single<TripSplitDatabase> {
        createDatabase(createDatabaseBuilder())
    }

    single<AppPreferences> { UserDefaultsAppPreferences() }
}