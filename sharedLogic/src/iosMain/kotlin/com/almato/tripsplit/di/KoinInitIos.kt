package com.almato.tripsplit.di

fun initKoinIos() {
    initKoin {
        modules(
            iOSAppModule,
        )
    }
}