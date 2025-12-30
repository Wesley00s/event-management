package com.example.wedding_planner.ui.screen.login

import com.example.wedding_planner.data.model.enums.LoginDestination

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccessLogin: Boolean = false,
    val error: String? = null,
    val navigationDestination: LoginDestination? = null
)
