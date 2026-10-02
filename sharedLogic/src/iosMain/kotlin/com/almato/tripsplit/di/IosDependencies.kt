package com.almato.tripsplit.di

import com.almato.tripsplit.data.local.preferences.AppPreferences
import com.almato.tripsplit.data.local.preferences.OverviewPreferences
import com.almato.tripsplit.usecase.CalculateTripSummary
import com.almato.tripsplit.domain.repository.TripRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class IosDependencies : KoinComponent {
    val calculateTripSummary: CalculateTripSummary by inject()
    val tripRepository: TripRepository by inject()

    val appPreferences: AppPreferences by inject()
    val overviewPreferences: OverviewPreferences by inject()
}