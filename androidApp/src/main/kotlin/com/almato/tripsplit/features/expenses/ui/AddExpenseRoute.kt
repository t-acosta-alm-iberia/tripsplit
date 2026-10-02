package com.almato.tripsplit.features.expenses.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almato.tripsplit.R
import com.almato.tripsplit.domain.model.Participant
import com.almato.tripsplit.domain.parseAmountCents
import com.almato.tripsplit.domain.splitEvenly
import com.almato.tripsplit.ui.TripSplitTheme
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal
import java.text.NumberFormat

@Composable
fun AddExpenseRoute(
    tripId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddExpenseViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentOnSaved by rememberUpdatedState(onSaved)
    LaunchedEffect(viewModel, tripId) { viewModel.loadTripData(tripId) }
    LaunchedEffect(state.isSaved) { if (state.isSaved) currentOnSaved() }
    AddExpenseScreen(
        state = state,
        onBack = onBack,
        onAmountChange = viewModel::updateAmount,
        onDescriptionChange = viewModel::updateDescription,
        onPayerChange = viewModel::selectPayer,
        onParticipantChange = viewModel::selectParticipant,
        onSave = viewModel::saveExpense,
        onRetry = { viewModel.loadTripData(tripId) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseScreen(
    state: AddExpenseUiState,
    onBack: () -> Unit,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPayerChange: (String) -> Unit,
    onParticipantChange: (String, Boolean) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Add expense") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.arrow_back_24), contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.loadError != null -> {
                    Text(state.loadError, color = MaterialTheme.colorScheme.error)
                    Button(onClick = onRetry) { Text("Try again") }
                }
                else -> {
                    val enabled = !state.isSaving && !state.isSaved
                    Text(state.tripName, style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = state.amountText,
                        onValueChange = onAmountChange,
                        label = { Text("Amount") },
                        placeholder = { Text("0.00") },
                        suffix = { Text("€") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = enabled,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Description") },
                        placeholder = { Text("Dinner") },
                        singleLine = true,
                        enabled = enabled,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    var payerMenuExpanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { payerMenuExpanded = true },
                            enabled = enabled && state.allParticipants.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            val payer = state.allParticipants.find { it.id == state.paidByParticipantId }
                            Text("Paid by: ${payer?.name ?: "Select participant"}")
                        }
                        DropdownMenu(
                            expanded = payerMenuExpanded && enabled,
                            onDismissRequest = { payerMenuExpanded = false },
                        ) {
                            state.allParticipants.forEach { participant ->
                                DropdownMenuItem(
                                    text = { Text(participant.name) },
                                    onClick = {
                                        onPayerChange(participant.id)
                                        payerMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                    Text("Split equally between", style = MaterialTheme.typography.titleMedium)
                    if (state.allParticipants.isEmpty()) Text("This trip has no participants.")
                    state.allParticipants.forEach { participant ->
                        ParticipantCheckboxRow(
                            name = participant.name,
                            selected = participant.id in state.selectedParticipantIds,
                            onSelectedChange = { onParticipantChange(participant.id, it) },
                            enabled = enabled,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    val cents = parseAmountCents(state.amountText)
                    if (cents != null && cents > 0 && state.selectedParticipantIds.isNotEmpty()) {
                        val formatter = NumberFormat.getNumberInstance().apply {
                            minimumFractionDigits = 2
                            maximumFractionDigits = 2
                        }
                        val shares = splitEvenly(cents, state.selectedParticipantIds)
                        val baseShareCents = shares.values.min()
                        val extraCentCount = shares.values.count { it > baseShareCents }
                        val share = formatter.format(BigDecimal.valueOf(baseShareCents, 2)) + " €"
                        Text(if (extraCentCount == 0) "$share per person"
                            else "$share per person; $extraCentCount receive an extra cent of the cost.")
                    }
                    state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Button(
                        onClick = onSave,
                        enabled = enabled && state.allParticipants.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(if (state.isSaving) "Saving…" else "Save expense") }
                }
            }
        }
    }
}

@Composable
private fun ParticipantCheckboxRow(
    name: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .toggleable(value = selected, enabled = enabled, role = Role.Checkbox, onValueChange = onSelectedChange)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = selected, onCheckedChange = null, enabled = enabled)
        Spacer(Modifier.width(12.dp))
        Text(name)
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AddExpenseScreenPreview() {
    TripSplitTheme {
        AddExpenseScreen(
            state = AddExpenseUiState(
                tripName = "Lisbon weekend", amountText = "90.00", description = "Dinner",
                allParticipants = listOf(Participant("1", "You"), Participant("2", "John"), Participant("3", "Dean")),
                selectedParticipantIds = setOf("1", "2", "3"), paidByParticipantId = "1", isLoading = false,
            ),
            onBack = {}, onAmountChange = {}, onDescriptionChange = {}, onPayerChange = {},
            onParticipantChange = { _, _ -> }, onSave = {}, onRetry = {},
        )
    }
}
