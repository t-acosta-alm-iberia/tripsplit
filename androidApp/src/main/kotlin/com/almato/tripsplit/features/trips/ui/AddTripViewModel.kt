package com.almato.tripsplit.features.trips.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almato.tripsplit.domain.model.Participant
import com.almato.tripsplit.domain.repository.TripRepository
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddTripUiState(
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
)

class AddTripViewModel(
    private val repository: TripRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val name = savedStateHandle.getStateFlow("name", "")
    val participantNames = savedStateHandle.getStateFlow("participantNames", "")
    private val mutableState = MutableStateFlow(AddTripUiState())
    val state = mutableState.asStateFlow()

    fun updateName(value: String) { savedStateHandle["name"] = value }
    fun updateParticipantNames(value: String) { savedStateHandle["participantNames"] = value }

    fun save() {
        if (state.value.isSaving || state.value.isSaved) return
        val tripName = name.value.trim()
        if (tripName.isEmpty()) {
            mutableState.value = AddTripUiState(errorMessage = "Enter a trip name.")
            return
        }
        val id = UUID.randomUUID().toString()
        val participants = listOf(Participant("$id:you", "You")) +
            participantNames.value.lines().map(String::trim).filter(String::isNotEmpty).map {
                Participant(UUID.randomUUID().toString(), it)
            }
        mutableState.value = AddTripUiState(isSaving = true)
        viewModelScope.launch {
            try {
                repository.createTrip(id, tripName, participants)
                mutableState.value = AddTripUiState(isSaved = true)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                mutableState.value = AddTripUiState(errorMessage = "Could not save the trip. Please try again.")
            }
        }
    }
}
