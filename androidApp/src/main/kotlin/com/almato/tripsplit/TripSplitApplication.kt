package com.almato.tripsplit

import android.app.Application
import com.almato.tripsplit.di.androidModule
import com.almato.tripsplit.di.appModule
import com.almato.tripsplit.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.plugin.module.dsl.module

class TripSplitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@TripSplitApplication)
            modules(androidModule)
        }
    }
}