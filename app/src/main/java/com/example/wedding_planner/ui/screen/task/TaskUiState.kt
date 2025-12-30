package com.example.wedding_planner.ui.screen.task

import com.example.wedding_planner.data.model.Task

data class TaskUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val progress: Float = 0f,
    val searchQuery: String = ""
)