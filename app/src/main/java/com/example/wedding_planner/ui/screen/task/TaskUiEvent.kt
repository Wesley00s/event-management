package com.example.wedding_planner.ui.screen.task

import com.example.wedding_planner.data.model.Task

sealed interface TaskUiEvent {
    data class SaveTask(val task: Task) : TaskUiEvent
    data class ToggleTask(val taskId: String, val isCompleted: Boolean) : TaskUiEvent
    data class DeleteTask(val taskId: String) : TaskUiEvent
    data class UpdateSearchQuery(val query: String) : TaskUiEvent
}