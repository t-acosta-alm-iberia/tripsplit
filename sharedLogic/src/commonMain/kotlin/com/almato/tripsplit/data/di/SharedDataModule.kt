package com.almato.tripsplit.data.di

import com.almato.tripsplit.data.local.TripSplitDatabase
import com.almato.tripsplit.data.local.dao.ExpenseDao
import com.almato.tripsplit.data.local.dao.TripDao
import com.almato.tripsplit.data.local.preferences.OverviewPreferences
import com.almato.tripsplit.data.repository.TripRepositoryImpl
import com.almato.tripsplit.domain.repository.TripRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val sharedDataModule = module {
    single<TripDao> { get<TripSplitDatabase>().tripDao() }
    single<ExpenseDao> { get<TripSplitDatabase>().expenseDao() }

    single<TripRepository> {
        TripRepositoryImpl(
            expenseDao = get(),
            tripDao = get(),
        )
    }

    // AppPreferences itself is registered per platform (AndroidModule, IosAppModule).
    factoryOf(::OverviewPreferences)
}