package com.almato.tripsplit.features.trips.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almato.tripsplit.domain.model.TripListItem
import com.almato.tripsplit.domain.repository.TripRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class TripsUiState(
    val trips: List<TripListItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class TripsViewModel(repository: TripRepository) : ViewModel() {
    val state = repository.observeTrips()
        .map { TripsUiState(trips = it, isLoading = false) }
        .catch { error ->
            if (error is CancellationException) throw error
            emit(TripsUiState(isLoading = false, errorMessage = "Could not load trips."))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TripsUiState())
}
