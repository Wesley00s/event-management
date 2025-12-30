package com.example.wedding_planner.ui.screen.budget

import com.example.wedding_planner.data.model.Expense

data class BudgetUiState(
    val isLoading: Boolean = false,
    val expenses: List<Expense> = emptyList(),
    val budgetLimit: Double = 0.0,
    val totalSpent: Double = 0.0,
    val totalPaid: Double = 0.0,
    val searchQuery: String = ""
)