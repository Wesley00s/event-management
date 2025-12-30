package com.example.wedding_planner.ui.screen.org

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wedding_planner.ui.components.OrgActionButtons
import com.example.wedding_planner.ui.components.OrgCard
import com.example.wedding_planner.ui.components.OrgEmptyState
import com.example.wedding_planner.ui.components.OrgInputDialog
import com.example.wedding_planner.ui.components.WeddingScreenBackground

@Composable
fun OrgScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: OrgViewModel = hiltViewModel(),
    onOrganizationSelected: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isOperationSuccess) {
        if (state.isOperationSuccess) {
            onOrganizationSelected()
            viewModel.onEvent(OrgUiEvent.ResetSuccessState)
        }
    }

    OrgScreen(
        modifier = modifier,
        uiState = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun OrgScreen(
    modifier: Modifier = Modifier,
    uiState: OrgUiState,
    onEvent: (OrgUiEvent) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(uiState.error)
            onEvent(OrgUiEvent.ClearError)
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        WeddingScreenBackground(
            modifier = modifier.padding(paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "Minhas Organizações",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Selecione ou crie um evento para gerenciar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    if (uiState.isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else if (uiState.organizations.isEmpty()) {
                        OrgEmptyState(
                            onCreateClick = { showCreateDialog = true },
                            onJoinClick = { showJoinDialog = true }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.organizations) { org ->
                                OrgCard(
                                    org = org,
                                    onClick = { onEvent(OrgUiEvent.SelectOrganization(org.id)) }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                                OrgActionButtons(
                                    onCreateClick = { showCreateDialog = true },
                                    onJoinClick = { showJoinDialog = true }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
            if (showCreateDialog) {
                OrgInputDialog(
                    title = "Novo Casamento",
                    label = "Nome do evento (ex: Ana & João)",
                    confirmText = "Criar",
                    onDismiss = { showCreateDialog = false },
                    onConfirm = { name ->
                        showCreateDialog = false
                        onEvent(OrgUiEvent.CreateOrganization(name))
                    }
                )
            }
            if (showJoinDialog) {
                OrgInputDialog(
                    title = "Entrar em Casamento",
                    label = "Código de Acesso (6 caracteres)",
                    confirmText = "Entrar",
                    onDismiss = { showJoinDialog = false },
                    onConfirm = { code ->
                        showJoinDialog = false
                        onEvent(OrgUiEvent.JoinOrganization(code))
                    }
                )
            }
        }
    }
}