package com.example.wedding_planner.ui.screen.task

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wedding_planner.data.model.Task
import com.example.wedding_planner.ui.components.HomeBackground
import com.example.wedding_planner.ui.components.SearchBar
import com.example.wedding_planner.ui.components.TaskDialog
import com.example.wedding_planner.ui.components.TaskItem
import com.example.wedding_planner.ui.components.TaskProgressHeader
import com.example.wedding_planner.ui.components.WeddingConfirmationDialog
import com.example.wedding_planner.ui.util.filteredGuestsBySearch

@Composable
fun TaskScreenRoute(
    viewModel: TaskViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TaskScreen(
        uiState = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun TaskScreen(
    uiState: TaskUiState,
    onEvent: (TaskUiEvent) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var isSearchActive by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        onEvent(TaskUiEvent.UpdateSearchQuery(""))
        focusManager.clearFocus()
    }
    val filteredTasks by remember(uiState.tasks, uiState.searchQuery) {
        derivedStateOf {
            if (uiState.searchQuery.isBlank()) {
                uiState.tasks
            } else {
                uiState.tasks.filter {
                    it.title.contains(uiState.searchQuery, ignoreCase = true)
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
                        taskToEdit = null
                        showDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nova Tarefa")
                }
            }
        }
    ) { padding ->
        HomeBackground(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedContent(
                    targetState = isSearchActive,
                    label = "HeaderTransition"
                ) { active ->
                    if (active) {
                        SearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = { onEvent(TaskUiEvent.UpdateSearchQuery(it)) },
                            onClose = {
                                isSearchActive = false
                                onEvent(TaskUiEvent.UpdateSearchQuery(""))
                                focusManager.clearFocus()
                            }
                        )
                    } else {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TAREFAS",
                                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                IconButton(
                                    onClick = { isSearchActive = true },
                                    modifier = Modifier.size(32.dp) 
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Buscar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            TaskProgressHeader(
                                progress = uiState.progress,
                                completed = uiState.completedCount,
                                total = uiState.totalCount
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (filteredTasks.isEmpty() && !uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (uiState.searchQuery.isNotEmpty()) "Nenhuma tarefa encontrada." else "Nenhuma tarefa pendente.",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp)
                    ) {
                        val grouped = filteredGuestsBySearch(filteredTasks)
                        grouped.forEach { (category, tasks) ->
                            item {
                                Text(
                                    text = category.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                            items(tasks, key = { it.id }) { task ->
                                TaskItem(
                                    task = task,
                                    onToggle = { onEvent(TaskUiEvent.ToggleTask(task.id, it)) },
                                    onClick = {
                                        taskToEdit = task
                                        showDialog = true
                                    },
                                    onDelete = { taskToDelete = task }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
        if (showDialog) {
            TaskDialog(
                taskToEdit = taskToEdit,
                onDismiss = { showDialog = false },
                onConfirm = { task ->
                    onEvent(TaskUiEvent.SaveTask(task))
                    showDialog = false
                }
            )
        }
        if (taskToDelete != null) {
            WeddingConfirmationDialog(
                title = "Excluir Tarefa?",
                message = "Deseja remover \"${taskToDelete?.title}\"?",
                confirmText = "Excluir",
                isDestructive = true,
                onDismiss = { taskToDelete = null },
                onConfirm = {
                    taskToDelete?.let { onEvent(TaskUiEvent.DeleteTask(it.id)) }
                    taskToDelete = null
                }
            )
        }
    }
}

