package com.almato.tripsplit.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(configuration: KoinAppDeclaration = {}) {
    startKoin {
        modules(appModule)
        configuration()
    }
}