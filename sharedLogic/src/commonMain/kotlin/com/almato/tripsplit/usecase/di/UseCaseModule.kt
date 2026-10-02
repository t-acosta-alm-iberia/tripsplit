package com.almato.tripsplit.usecase.di

import com.almato.tripsplit.usecase.AddExpense
import com.almato.tripsplit.usecase.CalculateTripSummary
import com.almato.tripsplit.usecase.ValidateExpense
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::CalculateTripSummary)
    factoryOf(::ValidateExpense)
    factoryOf(::AddExpense)
}