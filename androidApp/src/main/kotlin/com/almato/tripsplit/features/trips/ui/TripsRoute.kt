package com.almato.tripsplit.features.trips.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.almato.tripsplit.domain.model.TripListItem
import com.almato.tripsplit.ui.TripSplitTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun TripsRoute(
    onTripClick: (String) -> Unit,
    onAddTrip: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TripsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TripsScreen(
        state = state,
        onTripClick = onTripClick,
        onAddTrip = onAddTrip,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    state: TripsUiState,
    onTripClick: (String) -> Unit,
    onAddTrip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Trips") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAddTrip) { Text("Add trip") }
        },
    ) { padding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            state.errorMessage != null -> Text(
                text = state.errorMessage,
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp),
                color = MaterialTheme.colorScheme.error,
            )

            state.trips.isEmpty() -> Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("No trips yet", style = MaterialTheme.typography.titleMedium)

                Text("Your saved trips will appear here.")
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 88.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.trips, key = { it.id }) { trip ->
                    TripItemCard(
                        name = trip.name,
                        onClick = {
                            onTripClick(trip.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TripItemCard(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(name)
        }
    }
}

private val previewTrips = listOf(
    TripListItem("lisbon", "Lisbon weekend"),
    TripListItem("berlin", "Berlin with friends"),
    TripListItem("barcelona", "Barcelona summer holiday"),
)

@Preview(name = "Trips — Light", showBackground = true)
@Composable
private fun TripsScreenPreview() {
    TripSplitTheme(darkTheme = false) {
        TripsScreen(
            state = TripsUiState(trips = previewTrips, isLoading = false),
            onTripClick = {},
            onAddTrip = {},
        )
    }
}

@Preview(name = "Trips — Dark", showBackground = true)
@Composable
private fun TripsScreenDarkPreview() {
    TripSplitTheme(darkTheme = true) {
        TripsScreen(
            state = TripsUiState(trips = previewTrips, isLoading = false),
            onTripClick = {},
            onAddTrip = {},
        )
    }
}

@Preview(name = "Trips — Empty", showBackground = true)
@Composable
private fun TripsScreenEmptyPreview() {
    TripSplitTheme(darkTheme = false) {
        TripsScreen(
            state = TripsUiState(isLoading = false),
            onTripClick = {},
            onAddTrip = {},
        )
    }
}

@Preview(name = "Trips — Loading", showBackground = true)
@Composable
private fun TripsScreenLoadingPreview() {
    TripSplitTheme(darkTheme = false) {
        TripsScreen(
            state = TripsUiState(isLoading = true),
            onTripClick = {},
            onAddTrip = {},
        )
    }
}

@Preview(name = "Trips — Error", showBackground = true)
@Composable
private fun TripsScreenErrorPreview() {
    TripSplitTheme(darkTheme = false) {
        TripsScreen(
            state = TripsUiState(isLoading = false, errorMessage = "Could not load trips."),
            onTripClick = {},
            onAddTrip = {},
        )
    }
}
