package com.example.wedding_planner.ui.screen.splash

import com.example.wedding_planner.data.model.enums.SplashDestination

data class SplashUiState(
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false,
    val destination: SplashDestination? = null
)