package com.example.wedding_planner.ui.screen.budget

import com.example.wedding_planner.data.model.Expense

sealed interface BudgetUiEvent {
    data class SaveExpense(val expense: Expense) : BudgetUiEvent
    data class DeleteExpense(val expenseId: String) : BudgetUiEvent
    data class UpdateBudgetLimit(val limit: Double) : BudgetUiEvent
    data class UpdateSearchQuery(val query: String) : BudgetUiEvent
}