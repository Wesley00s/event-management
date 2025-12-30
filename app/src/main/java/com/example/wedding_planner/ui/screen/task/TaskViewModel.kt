package com.example.wedding_planner.ui.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wedding_planner.data.model.Task
import com.example.wedding_planner.data.repo.OrganizationRepository
import com.example.wedding_planner.data.repo.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val orgRepository: OrganizationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState = _uiState.asStateFlow()
    private var currentOrgId: String? = null

    init {
        loadTasks()
    }

    fun onEvent(event: TaskUiEvent) {
        when (event) {
            is TaskUiEvent.SaveTask -> saveTask(event.task)
            is TaskUiEvent.ToggleTask -> toggleTask(event.taskId, event.isCompleted)
            is TaskUiEvent.DeleteTask -> deleteTask(event.taskId)
            is TaskUiEvent.UpdateSearchQuery -> _uiState.update { it.copy(searchQuery = event.query) }
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            if (currentOrgId == null) {
                currentOrgId = orgRepository.getCurrentOrganization().getOrNull()?.id
            }
            val orgId = currentOrgId ?: return@launch

            taskRepository.getTasks(orgId).onSuccess { tasks ->
                updateStats(tasks)
            }
        }
    }

    private fun updateStats(tasks: List<Task>) {
        val completed = tasks.count { it.isCompleted }
        val total = tasks.size
        _uiState.update {
            it.copy(
                isLoading = false,
                tasks = tasks,
                completedCount = completed,
                totalCount = total,
                progress = if (total > 0) completed.toFloat() / total else 0f
            )
        }
    }

    private fun saveTask(task: Task) {
        val orgId = currentOrgId ?: return
        viewModelScope.launch {
            taskRepository.saveTask(orgId, task).onSuccess { loadTasks() }
        }
    }

    private fun toggleTask(taskId: String, isCompleted: Boolean) {
        val orgId = currentOrgId ?: return
        _uiState.update { state ->
            val updatedList = state.tasks.map {
                if (it.id == taskId) it.copy(isCompleted = isCompleted) else it
            }
            val completed = updatedList.count { it.isCompleted }
            val total = updatedList.size
            state.copy(
                tasks = updatedList,
                completedCount = completed,
                progress = if (total > 0) completed.toFloat() / total else 0f
            )
        }
        viewModelScope.launch {
            taskRepository.toggleTaskStatus(orgId, taskId, isCompleted)
        }
    }

    private fun deleteTask(taskId: String) {
        val orgId = currentOrgId ?: return
        viewModelScope.launch {
            taskRepository.deleteTask(orgId, taskId).onSuccess { loadTasks() }
        }
    }
}