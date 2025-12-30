package com.example.wedding_planner.ui.screen.home

data class HomeUiState(
    val isLoading: Boolean = true,
    val orgName: String = "",
    val daysRemaining: Long? = null,
    val guestCount: Int = 0,
    val taskProgress: Float = 0f,
    val budgetSpent: Double = 0.0,
    val budgetLimit: Double = 0.0
)