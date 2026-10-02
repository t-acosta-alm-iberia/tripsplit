package com.almato.tripsplit.features.overview.di

import com.almato.tripsplit.features.overview.ui.OverviewViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val overviewFeatureModule = module {
    viewModelOf(::OverviewViewModel)
}