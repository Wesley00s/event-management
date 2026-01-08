package com.example.wedding_planner.ui.screen.guest

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wedding_planner.data.model.Guest
import com.example.wedding_planner.data.model.enums.GuestSide
import com.example.wedding_planner.ui.components.AddGuestDialog
import com.example.wedding_planner.ui.components.GuestItemCard
import com.example.wedding_planner.ui.components.HomeBackground
import com.example.wedding_planner.ui.components.WeddingConfirmationDialog

@Composable
fun GuestScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: GuestViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    GuestScreen(
        modifier = modifier,
        uiState = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GuestScreen(
    modifier: Modifier = Modifier,
    uiState: GuestUiState,
    onEvent: (GuestUiEvent) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var guestToEdit by remember { mutableStateOf<Guest?>(null) }
    var guestToDelete by remember { mutableStateOf<Guest?>(null) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        searchQuery = ""
        focusManager.clearFocus()
    }

    val tabs = remember(uiState.guests) {
        val totalCount = uiState.guests.size
        val brideCount = uiState.guests.count { it.side == GuestSide.BRIDE }
        val groomCount = uiState.guests.count { it.side == GuestSide.GROOM }

        listOf(
            "Todos ($totalCount)",
            "Noiva ($brideCount)",
            "Noivo ($groomCount)"
        )
    }

    val filteredGuests by remember(selectedTabIndex, uiState.guests, searchQuery) {
        derivedStateOf {
            if (searchQuery.isNotBlank()) {
                uiState.guests.filter { it.name.contains(searchQuery, ignoreCase = true) }
            } else {
                when (selectedTabIndex) {
                    0 -> uiState.guests
                    1 -> uiState.guests.filter { it.side == GuestSide.BRIDE }
                    2 -> uiState.guests.filter { it.side == GuestSide.GROOM }
                    else -> emptyList()
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (!isSearchActive) {
                FloatingActionButton(
                    onClick = {
                        guestToEdit = null
                        showDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Guest")
                }
            }
        }
    ) { padding ->
        HomeBackground(modifier = modifier.padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(16.dp))


                AnimatedContent(
                    targetState = isSearchActive,
                    transitionSpec = {
                        if (targetState) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut())
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut())
                        }
                    },
                    label = "SearchTransition"
                ) { active ->
                    if (active) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                placeholder = { Text("Digite o nome...") },
                                leadingIcon = {
                                    IconButton(onClick = {
                                        isSearchActive = false
                                        searchQuery = ""
                                        focusManager.clearFocus()
                                    }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                    }
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, "Limpar")
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.5f
                                    ),
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TabRow(
                                selectedTabIndex = selectedTabIndex,
                                modifier = Modifier.weight(1f),
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.primary,
                                indicator = { tabPositions ->
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                divider = {}
                            ) {
                                tabs.forEachIndexed { index, title ->
                                    Tab(
                                        selected = selectedTabIndex == index,
                                        onClick = { selectedTabIndex = index },
                                        text = {
                                            Text(
                                                text = title,
                                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        },
                                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(
                                onClick = { isSearchActive = true },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (filteredGuests.isEmpty()) {
                    val emptyMessage = when {
                        searchQuery.isNotEmpty() -> "Nenhum convidado encontrado para \"$searchQuery\"."
                        selectedTabIndex == 1 -> "Nenhum convidado da noiva."
                        selectedTabIndex == 2 -> "Nenhum convidado do noivo."
                        else -> "Nenhum convidado ainda.\nToque em + para adicionar."
                    }
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = emptyMessage,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        items(filteredGuests, key = { it.id }) { guest ->
                            GuestItemCard(
                                guest = guest,
                                onClick = {
                                    guestToEdit = guest
                                    showDialog = true
                                },
                                onUpdateRole = { role ->
                                    onEvent(GuestUiEvent.UpdateRole(guest.id, role))
                                },
                                onDelete = { guestToDelete = guest }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }


        if (showDialog) {
            AddGuestDialog(
                guestToEdit = guestToEdit,
                onDismiss = { showDialog = false },
                onConfirm = { guest ->
                    onEvent(GuestUiEvent.SaveGuest(guest))
                    showDialog = false
                }
            )
        }

        if (guestToDelete != null) {
            WeddingConfirmationDialog(
                title = "Excluir Convidado?",
                message = "Deseja remover ${guestToDelete?.name} da lista?",
                confirmText = "Excluir",
                isDestructive = true,
                onDismiss = { guestToDelete = null },
                onConfirm = {
                    guestToDelete?.let { onEvent(GuestUiEvent.DeleteGuest(it.id)) }
                    guestToDelete = null
                }
            )
        }
    }
}