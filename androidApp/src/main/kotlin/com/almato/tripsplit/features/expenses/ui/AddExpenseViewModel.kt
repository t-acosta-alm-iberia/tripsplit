package com.almato.tripsplit.features.expenses.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almato.tripsplit.domain.model.Participant
import com.almato.tripsplit.domain.parseAmountCents
import com.almato.tripsplit.domain.repository.TripRepository
import com.almato.tripsplit.usecase.AddExpense
import com.almato.tripsplit.usecase.ExpenseValidationError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddExpenseUiState(
    val amountText: String = "",
    val description: String = "",
    val tripName: String = "",
    val allParticipants: List<Participant> = emptyList(),
    val selectedParticipantIds: Set<String> = emptySet(),
    val paidByParticipantId: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val loadError: String? = null,
    val errorMessage: String? = null,
)

class AddExpenseViewModel(
    private val tripRepository: TripRepository,
    private val addExpense: AddExpense,
) : ViewModel() {
    private val _state = MutableStateFlow(AddExpenseUiState())
    val state = _state.asStateFlow()
    private var tripId: String? = null
    private var loadJob: Job? = null
    private var selectionInitialized = false

    fun loadTripData(id: String) {
        if (tripId == id && loadJob?.isActive == true) return
        loadJob?.cancel()
        if (tripId != id) {
            selectionInitialized = false
            _state.value = AddExpenseUiState()
        }
        tripId = id
        _state.update { it.copy(isLoading = true, loadError = null) }
        loadJob = viewModelScope.launch {
            try {
                tripRepository.observeTrip(id).collect { trip ->
                    if (trip == null) {
                        _state.update { it.copy(isLoading = false, loadError = "Trip not found.") }
                    } else {
                        val ids = trip.participants.map { it.id }.toSet()
                        _state.update { current ->
                            current.copy(
                                tripName = trip.name,
                                allParticipants = trip.participants,
                                selectedParticipantIds = if (!selectionInitialized) ids
                                    else current.selectedParticipantIds.intersect(ids),
                                paidByParticipantId = current.paidByParticipantId?.takeIf { it in ids }
                                    ?: "$id:you".takeIf { it in ids }
                                    ?: trip.participants.firstOrNull()?.id,
                                isLoading = false,
                                loadError = null,
                            )
                        }
                        selectionInitialized = true
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _state.update { it.copy(isLoading = false, loadError = "Could not load this trip.") }
            }
        }
    }

    fun updateAmount(value: String) { _state.update { it.copy(amountText = value, errorMessage = null) } }
    fun updateDescription(value: String) { _state.update { it.copy(description = value, errorMessage = null) } }
    fun selectPayer(id: String) { _state.update { it.copy(paidByParticipantId = id, errorMessage = null) } }
    fun selectParticipant(id: String, selected: Boolean) {
        _state.update {
            it.copy(
                selectedParticipantIds = if (selected) it.selectedParticipantIds + id else it.selectedParticipantIds - id,
                errorMessage = null,
            )
        }
    }

    fun saveExpense() {
        val current = state.value
        val id = tripId ?: return
        if (current.isLoading || current.isSaving || current.isSaved || current.loadError != null) return
        _state.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val error = addExpense(
                    tripId = id,
                    description = current.description,
                    amountCents = parseAmountCents(current.amountText),
                    paidByParticipantId = current.paidByParticipantId,
                    splitParticipantIds = current.selectedParticipantIds,
                )
                _state.update {
                    if (error != null) it.copy(isSaving = false, errorMessage = error.message())
                    else it.copy(isSaving = false, isSaved = true)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _state.update { it.copy(isSaving = false, errorMessage = "Could not save the expense. Please try again.") }
            }
        }
    }
}

private fun ExpenseValidationError.message(): String = when (this) {
    ExpenseValidationError.BLANK_DESCRIPTION -> "Enter a description."
    ExpenseValidationError.INVALID_AMOUNT,
    ExpenseValidationError.NON_POSITIVE_AMOUNT -> "Enter a positive amount with up to two decimal places."
    ExpenseValidationError.UNKNOWN_PAYER -> "Select who paid."
    ExpenseValidationError.NO_PARTICIPANTS -> "Select at least one person to share the expense."
    ExpenseValidationError.UNKNOWN_PARTICIPANT -> "Check the selected participants."
}
