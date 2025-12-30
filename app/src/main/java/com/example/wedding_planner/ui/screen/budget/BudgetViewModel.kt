package com.example.wedding_planner.ui.screen.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wedding_planner.data.model.Expense
import com.example.wedding_planner.data.repo.BudgetRepository
import com.example.wedding_planner.data.repo.OrganizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val orgRepository: OrganizationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()
    private var currentOrgId: String? = null

    init {
        loadData()
    }
    
    fun onEvent(event: BudgetUiEvent) {
        when(event) {
            is BudgetUiEvent.SaveExpense -> saveExpense(event.expense)
            is BudgetUiEvent.DeleteExpense -> deleteExpense(event.expenseId)
            is BudgetUiEvent.UpdateBudgetLimit -> updateBudgetLimit(event.limit)
            is BudgetUiEvent.UpdateSearchQuery -> _uiState.update { it.copy(searchQuery = event.query) }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val orgResult = orgRepository.getCurrentOrganization().getOrNull() ?: return@launch
            currentOrgId = orgResult.id
            
            val limit = orgResult.budgetLimit ?: 0.0

            budgetRepository.getExpenses(orgResult.id).onSuccess { list ->
                val spent = list.sumOf { it.actualCost }
                val paid = list.sumOf { it.paidAmount }
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        expenses = list,
                        budgetLimit = limit,
                        totalSpent = spent,
                        totalPaid = paid
                    )
                }
            }
        }
    }

    private fun saveExpense(expense: Expense) {
        val orgId = currentOrgId ?: return
        viewModelScope.launch {
            budgetRepository.saveExpense(orgId, expense).onSuccess { loadData() }
        }
    }

    private fun deleteExpense(id: String) {
        val orgId = currentOrgId ?: return
        viewModelScope.launch {
            budgetRepository.deleteExpense(orgId, id).onSuccess { loadData() }
        }
    }

    private fun updateBudgetLimit(limit: Double) {
        val orgId = currentOrgId ?: return
        viewModelScope.launch {
            budgetRepository.updateBudgetLimit(orgId, limit).onSuccess { loadData() }
        }
    }
}