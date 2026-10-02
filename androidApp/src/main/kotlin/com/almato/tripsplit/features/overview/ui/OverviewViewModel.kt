package com.almato.tripsplit.features.overview.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almato.tripsplit.usecase.CalculateTripSummary
import com.almato.tripsplit.domain.model.Trip
import com.almato.tripsplit.domain.repository.TripRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.NumberFormat

enum class BalanceStatus {
    OWED_TO_YOU,
    YOU_OWE,
    SETTLED,
}

data class OverviewUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val tripName: String = "",
    val participantNames: String = "",
    val totalExpenses: String = "",
    val yourShare: String = "",
    val balanceText: String = "",
    val balanceStatus: BalanceStatus = BalanceStatus.SETTLED,
    val expenses: List<ExpenseRowUiState> = emptyList(),
)

data class ExpenseRowUiState(
    val id: String,
    val name: String,
    val amount: String,
    val paidBy: String,
    val participantCount: Int,
)

class OverviewViewModel(
    private val repository: TripRepository,
    private val calculateTripSummary: CalculateTripSummary
) : ViewModel() {

    private val _state = MutableStateFlow(OverviewUiState())
    val state: StateFlow<OverviewUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    fun loadTrip(tripId: String, currentParticipantId: String) {
        loadJob?.cancel()
        _state.value = OverviewUiState(isLoading = true)
        loadJob = viewModelScope.launch {
            try {
                repository.observeTrip(tripId).collect { trip ->
                    _state.value = if (trip == null) {
                        OverviewUiState(errorMessage = "Trip not found.")
                    } else {
                        try {
                            mapTripToUiState(trip, currentParticipantId)
                        } catch (exception: IllegalArgumentException) {
                            OverviewUiState(errorMessage = "The trip contains invalid participant or expense data.")
                        }
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                ensureActive()
                _state.value = OverviewUiState(
                    errorMessage = "Could not load the trip. Please try again."
                )
            }
        }
    }

    private fun mapTripToUiState(trip: Trip, currentParticipantId: String): OverviewUiState {
        val summary = calculateTripSummary(trip, currentParticipantId)
        val participantsById = trip.participants.associateBy { it.id }


        // TODO(workshop): Consider a reusable presentation money formatter. Locale-specific
        val formatter = NumberFormat.getNumberInstance().apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }

        fun formatAmount(cents: Long): String =
            "${formatter.format(BigDecimal.valueOf(cents, 2))}\u00A0€"

        val balanceStatus = when {
            summary.balanceCents > 0 -> BalanceStatus.OWED_TO_YOU
            summary.balanceCents < 0 -> BalanceStatus.YOU_OWE
            else -> BalanceStatus.SETTLED
        }

        val balanceText = when (balanceStatus) {
            BalanceStatus.OWED_TO_YOU -> "You are owed ${formatAmount(summary.balanceCents)}"
            BalanceStatus.YOU_OWE -> "You owe ${formatAmount(-summary.balanceCents)}"
            BalanceStatus.SETTLED -> "Settled up"
        }

        return OverviewUiState(
            tripName = trip.name,
            participantNames = trip.participants.joinToString(", ") {
                if (it.id == currentParticipantId) "You" else it.name
            },
            totalExpenses = formatAmount(summary.totalExpensesCents),
            yourShare = formatAmount(summary.shareCents),
            balanceText = balanceText,
            balanceStatus = balanceStatus,
            expenses = trip.expenses.map { expense ->
                ExpenseRowUiState(
                    id = expense.id,
                    name = expense.description,
                    amount = formatAmount(expense.amountCents),
                    paidBy = if (expense.paidByParticipantId == currentParticipantId) {
                        "You"
                    } else {
                        participantsById.getValue(expense.paidByParticipantId).name
                    },
                    participantCount = expense.participantIds.size,
                )
            },
        )
    }

}