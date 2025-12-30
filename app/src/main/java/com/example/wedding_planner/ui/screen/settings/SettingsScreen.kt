package com.example.wedding_planner.ui.screen.settings

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wedding_planner.data.model.User
import com.example.wedding_planner.ui.components.HomeBackground
import com.example.wedding_planner.ui.components.InvitePartnerSection
import com.example.wedding_planner.ui.components.OrgInputDialog
import com.example.wedding_planner.ui.components.ParticipantRow
import com.example.wedding_planner.ui.components.SettingsRowItem
import com.example.wedding_planner.ui.components.WeddingConfirmationDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreenRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = state,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit,
    onBackClick: () -> Unit
) {
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showEditLocationDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<User?>(null) }
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onEvent(SettingsUiEvent.UpdateDate(calendar.timeInMillis))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "CONFIGURAÇÕES",
                        style = MaterialTheme.typography.titleMedium.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        HomeBackground(modifier = Modifier.padding(paddingValues)) {
            if (uiState.isLoading && uiState.organization == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "DETALHES DO EVENTO",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            SettingsRowItem(
                                icon = Icons.Default.Edit,
                                label = "Nome",
                                value = uiState.organization?.name ?: "Definir nome",
                                onClick = { showEditNameDialog = true }
                            )
                            HorizontalDivider(
                                Modifier
                                    .padding(vertical = 8.dp)
                                    .alpha(0.5f)
                            )
                            val dateString = remember(uiState.organization?.date) {
                                uiState.organization?.date?.let {
                                    SimpleDateFormat(
                                        "dd 'de' MMMM 'de' yyyy",
                                        Locale.forLanguageTag("pt-BR")
                                    ).format(
                                        Date(it)
                                    )
                                } ?: "Definir data"
                            }
                            SettingsRowItem(
                                icon = Icons.Default.CalendarMonth,
                                label = "Data",
                                value = dateString,
                                onClick = { datePickerDialog.show() }
                            )
                            HorizontalDivider(
                                Modifier
                                    .padding(vertical = 8.dp)
                                    .alpha(0.5f)
                            )
                            SettingsRowItem(
                                icon = Icons.Default.LocationOn,
                                label = "Local",
                                value = uiState.organization?.location ?: "Definir local",
                                onClick = { showEditLocationDialog = true }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "PARTICIPANTES",
                            style = MaterialTheme.typography.titleSmall.copy(letterSpacing = 1.5.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        if (uiState.isLoading) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            Column(modifier = Modifier.padding(8.dp)) {
                                uiState.participants.forEach { user ->
                                    ParticipantRow(
                                        user = user,
                                        isOwner = uiState.organization?.ownerId == uiState.currentUserId,
                                        isMe = user.uid == uiState.currentUserId,
                                        onDeleteClick = { userToDelete = user }
                                    )
                                    if (user != uiState.participants.last()) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    val memberCount = uiState.organization?.members?.size ?: 0
                    if (memberCount >= 2) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                    alpha = 0.2f
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Equipe completa! Noivo e Noiva conectados.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    } else {
                        InvitePartnerSection(
                            accessCode = uiState.organization?.accessCode ?: "",
                            clipboard = clipboard,
                            scope = scope,
                            context = context
                        )
                    }
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
        if (showEditNameDialog) {
            OrgInputDialog(
                title = "Editar Nome",
                label = "Novo nome do evento",
                confirmText = "Salvar",
                icon = Icons.Default.Edit,
                onDismiss = { showEditNameDialog = false },
                onConfirm = { newName ->
                    onEvent(SettingsUiEvent.UpdateName(newName))
                    showEditNameDialog = false
                }
            )
        }
        if (showEditLocationDialog) {
            OrgInputDialog(
                title = "Definir Local",
                label = "Cidade ou Local da Cerimônia",
                confirmText = "Salvar",
                onDismiss = { showEditLocationDialog = false },
                onConfirm = { newLocation ->
                    onEvent(SettingsUiEvent.UpdateLocation(newLocation))
                    showEditLocationDialog = false
                }
            )
        }
        if (userToDelete != null) {
            WeddingConfirmationDialog(
                title = "Remover parceiro?",
                message = "Deseja remover ${userToDelete?.name} da organização? Ele perderá o acesso ao planejamento.",
                confirmText = "Remover",
                isDestructive = true,
                onDismiss = { userToDelete = null },
                onConfirm = {
                    userToDelete?.let { onEvent(SettingsUiEvent.RemovePartner(it.uid)) }
                    userToDelete = null
                }
            )
        }
    }
}