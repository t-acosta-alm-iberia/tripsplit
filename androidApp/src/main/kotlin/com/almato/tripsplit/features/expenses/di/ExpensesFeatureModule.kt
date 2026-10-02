package com.almato.tripsplit.features.expenses.di

import com.almato.tripsplit.features.expenses.ui.AddExpenseViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val expensesFeatureModule = module {
    viewModelOf(::AddExpenseViewModel)
}
