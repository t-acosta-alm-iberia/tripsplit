package com.almato.tripsplit

import androidx.compose.runtime.Composable
import com.almato.tripsplit.navigation.AppNavDisplay
import com.almato.tripsplit.ui.TripSplitTheme

@Composable
fun App() {
    TripSplitTheme {
        AppNavDisplay()
    }
}
