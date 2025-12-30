package com.example.wedding_planner.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wedding_planner.data.repo.BudgetRepository
import com.example.wedding_planner.data.repo.GuestRepository
import com.example.wedding_planner.data.repo.OrganizationRepository
import com.example.wedding_planner.data.repo.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val orgRepository: OrganizationRepository,
    private val guestRepository: GuestRepository,
    private val taskRepository: TaskRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.Refresh -> loadDashboard()
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val orgResult = orgRepository.getCurrentOrganization().getOrNull()

            if (orgResult == null) {
                _uiState.update { it.copy(isLoading = false, orgName = "Bem-vindo") }
                return@launch
            }
            val orgId = orgResult.id
            val daysDiff = orgResult.date?.let { eventDate ->
                val diff = eventDate - System.currentTimeMillis()
                
                if (diff > 0) TimeUnit.MILLISECONDS.toDays(diff) else 0
            }
            val guests = guestRepository.getGuests(orgId).getOrDefault(emptyList())
            val tasks = taskRepository.getTasks(orgId).getOrDefault(emptyList())
            val completedTasks = tasks.count { it.isCompleted }
            val progress = if (tasks.isNotEmpty()) completedTasks.toFloat() / tasks.size else 0f

            val expenses = budgetRepository.getExpenses(orgId).getOrDefault(emptyList())
            val spent = expenses.sumOf { it.actualCost } 
            val limit = orgResult.budgetLimit ?: 0.0

            _uiState.update {
                it.copy(
                    isLoading = false,
                    orgName = orgResult.name,
                    daysRemaining = daysDiff,
                    guestCount = guests.size,
                    taskProgress = progress,
                    budgetSpent = spent,
                    budgetLimit = limit
                )
            }
        }
    }
}