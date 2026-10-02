package com.almato.tripsplit.di

import com.almato.tripsplit.data.di.sharedDataModule
import com.almato.tripsplit.usecase.di.useCaseModule
import org.koin.dsl.module

val appModule = module {
    includes(
        sharedDataModule,
        useCaseModule
    )
}