package com.almato.tripsplit.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.almato.tripsplit.features.expenses.ui.AddExpenseRoute
import com.almato.tripsplit.features.overview.ui.OverviewRoute
import com.almato.tripsplit.features.trips.ui.TripsRoute
import com.almato.tripsplit.features.trips.ui.AddTripRoute
import kotlin.collections.listOf

@Composable
fun AppNavDisplay() {
    val backStack = rememberNavBackStack(
        Destination.Trips
    )

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeAt(backStack.lastIndex)
            }
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(
                targetOffsetX = { -it })
        },
        popTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        entryProvider = entryProvider {
            entry<Destination.Trips> {
                TripsRoute(
                    onTripClick = { tripId -> backStack.add(Destination.Overview(tripId)) },
                    onAddTrip = { backStack.add(Destination.AddTrip) },
                )
            }

            entry<Destination.AddTrip> {
                AddTripRoute(
                    onBack = { backStack.removeAt(backStack.lastIndex) },
                    onSaved = { backStack.removeAt(backStack.lastIndex) },
                )
            }

            entry<Destination.Overview> { destination ->
                OverviewRoute(
                    tripId = destination.tripId,
                    currentParticipantId = "${destination.tripId}:you",
                    onAddExpense = {
                        backStack.add(
                            Destination.AddExpense(
                                destination.tripId
                            )
                        )
                    }
                )
            }

            entry<Destination.AddExpense> { destination ->
                AddExpenseRoute(
                    tripId = destination.tripId,
                    onSaved = { backStack.removeAt(backStack.lastIndex) },
                    onBack = {
                        backStack.removeAt(backStack.lastIndex)
                    }
                )
            }
        }
    )
}