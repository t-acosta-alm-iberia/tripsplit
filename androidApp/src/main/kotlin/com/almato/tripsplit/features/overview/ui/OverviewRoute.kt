package com.almato.tripsplit.features.overview.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almato.tripsplit.data.local.preferences.AppPreferences
import com.almato.tripsplit.data.local.preferences.OverviewPreferences
import com.almato.tripsplit.ui.TripSplitTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun OverviewRoute(
    tripId: String,
    currentParticipantId: String,
    modifier: Modifier = Modifier,
    onAddExpense: () -> Unit,
    onViewBalances: (() -> Unit)? = null,
    viewModel: OverviewViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appPreferences = koinInject<AppPreferences>()
    var showExpenses by rememberSaveable {
        mutableStateOf(appPreferences.getBoolean("show_expenses", defaultValue = true))
    }
    //val overviewPreferences = koinInject<OverviewPreferences>()
    //var showExpenses by rememberSaveable { mutableStateOf(overviewPreferences.showExpenses) }

    LaunchedEffect(viewModel, tripId, currentParticipantId) {
        viewModel.loadTrip(tripId, currentParticipantId)
    }

    Scaffold(
        floatingActionButton = {
            AddExpenseFAB(onAddExpense)
        }
    ) { innerPadding ->
        OverviewScreen(
            modifier = modifier.padding(innerPadding),
            state = state,
            onViewBalances = onViewBalances,
            onRetry = { viewModel.loadTrip(tripId, currentParticipantId) },
            showExpenses = showExpenses,
            onShowExpensesChange = {
                showExpenses = it
                appPreferences.getBoolean("show_expenses", defaultValue = true)
                //overviewPreferences.showExpenses = it
            },
        )
    }

}

@Composable
private fun OverviewScreen(
    state: OverviewUiState,
    onRetry: () -> Unit,
    onViewBalances: (() -> Unit)?,
    modifier: Modifier = Modifier,
    showExpenses: Boolean = true,
    onShowExpensesChange: (Boolean) -> Unit = {},
) {
    when {
        state.errorMessage != null -> {
            Column(
                modifier = modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(state.errorMessage, color = MaterialTheme.colorScheme.error)
                Button(onClick = onRetry) { Text("Try again") }
            }
        }

        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = modifier
                        .padding(24.dp)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                    Text("Loading trip…")
                }
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(state.tripName, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "${state.participantNames} · EUR",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                item {
                    BalanceCard(
                        totalExpenses = state.totalExpenses,
                        balanceText = state.balanceText,
                        balanceStatus = state.balanceStatus,
                        onViewBalances = onViewBalances,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Expenses",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(checked = showExpenses, onCheckedChange = onShowExpensesChange)
                    }
                }

                if (showExpenses) {
                    if (state.expenses.isEmpty()) {
                        item {
                            Text(
                                "No expenses yet.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    items(state.expenses, key = { it.id }) { expense ->
                        ExpenseRow(
                            title = expense.name,
                            amount = expense.amount,
                            paidBy = expense.paidBy,
                            participantCount = expense.participantCount,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(
    totalExpenses: String,
    balanceText: String,
    balanceStatus: BalanceStatus,
    onViewBalances: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val balanceColor = when (balanceStatus) {
        BalanceStatus.OWED_TO_YOU -> MaterialTheme.colorScheme.secondary
        BalanceStatus.YOU_OWE -> MaterialTheme.colorScheme.tertiary
        BalanceStatus.SETTLED -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    "Total trip expenses",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(totalExpenses, style = MaterialTheme.typography.titleMedium)
            }

            HorizontalDivider()

            Text(
                balanceText,
                style = MaterialTheme.typography.headlineSmall,
                color = balanceColor,
            )

            // Supply this callback from the navigation layer when the destination exists.
            TextButton(
                onClick = { onViewBalances?.invoke() },
                enabled = onViewBalances != null,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("View balances")
            }
        }
    }
}

@Composable
private fun ExpenseRow(
    title: String,
    amount: String,
    paidBy: String,
    participantCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
            )

            Text(amount, style = MaterialTheme.typography.titleMedium)
        }

        Text(
            "$paidBy paid · Split between $participantCount",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AddExpenseFAB(
    onAddExpense: () -> Unit
) {
    FloatingActionButton(
        onClick = {
            onAddExpense()
        }
    ) {
        Text("+", style = MaterialTheme.typography.headlineMedium)
    }
}

private val PreviewState = OverviewUiState(
    tripName = "Lisbon Weekend",
    participantNames = "You, John, Dean",
    totalExpenses = "120.00\u00A0€",
    yourShare = "40.00\u00A0€",
    balanceText = "You are owed 50.00\u00A0€",
    balanceStatus = BalanceStatus.OWED_TO_YOU,
    expenses = listOf(
        ExpenseRowUiState("1", "Dinner", "90.00\u00A0€", "You", 3),
        ExpenseRowUiState("2", "Taxi", "30.00\u00A0€", "John", 3),
    ),
)

@Preview(showBackground = true)
@Composable
private fun OverviewScreenPreview() {
    TripSplitTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            OverviewScreen(
                state = PreviewState,
                onRetry = {},
                onViewBalances = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview(name = "Dark", showBackground = true)
@Composable
private fun OverviewScreenDarkPreview() {
    TripSplitTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            OverviewScreen(
                state = PreviewState,
                onRetry = {},
                onViewBalances = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
