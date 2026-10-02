package com.almato.tripsplit.di

import com.almato.tripsplit.data.local.TripSplitDatabase
import com.almato.tripsplit.data.local.createDatabase
import com.almato.tripsplit.data.local.createDatabaseBuilder
import com.almato.tripsplit.data.local.preferences.AppPreferences
import com.almato.tripsplit.data.local.preferences.SharedPreferencesAppPreferences
import com.almato.tripsplit.features.expenses.di.expensesFeatureModule
import com.almato.tripsplit.features.overview.di.overviewFeatureModule
import com.almato.tripsplit.features.trips.di.tripsFeatureModule
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    includes(
        expensesFeatureModule,
        overviewFeatureModule,
        tripsFeatureModule
    )

    single<TripSplitDatabase> {
        createDatabase(
            createDatabaseBuilder(androidContext())
        )
    }

    single<AppPreferences> { SharedPreferencesAppPreferences(androidContext()) }
}