package com.almato.tripsplit.features.trips.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almato.tripsplit.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddTripRoute(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddTripViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val participantNames by viewModel.participantNames.collectAsStateWithLifecycle()
    val currentOnSaved by rememberUpdatedState(onSaved)
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) currentOnSaved()
    }
    AddTripScreen(
        name = name,
        participantNames = participantNames,
        state = state,
        onNameChange = viewModel::updateName,
        onParticipantsChange = viewModel::updateParticipantNames,
        onSave = viewModel::save,
        onBack = onBack,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripScreen(
    name: String,
    participantNames: String,
    state: AddTripUiState,
    onNameChange: (String) -> Unit,
    onParticipantsChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Add trip") },
                navigationIcon = {
                    IconButton(onClick = {
                        onBack()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24),
                            contentDescription = null
                        )
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
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Trip name") },
                placeholder = { Text("Lisbon weekend") },
                singleLine = true,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            Text("Participants", style = MaterialTheme.typography.titleMedium)
            Text("You are included automatically.")
            OutlinedTextField(
                value = participantNames,
                onValueChange = onParticipantsChange,
                label = { Text("Other participants") },
                supportingText = { Text("Enter one name per line. You can leave this empty.") },
                minLines = 3,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            state.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = onSave,
                enabled = name.isNotBlank() && !state.isSaving && !state.isSaved,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isSaving) "Saving…" else "Save trip")
            }
        }
    }
}
