package com.almato.tripsplit.features.trips.di

import com.almato.tripsplit.features.trips.ui.TripsViewModel
import com.almato.tripsplit.features.trips.ui.AddTripViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tripsFeatureModule = module {
    viewModelOf(::TripsViewModel)
    viewModelOf(::AddTripViewModel)
}
