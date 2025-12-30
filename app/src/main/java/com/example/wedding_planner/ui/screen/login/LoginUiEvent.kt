package com.example.wedding_planner.ui.screen.login

import android.content.Context

sealed interface LoginUiEvent {
    data class PerformLogin(val context: Context): LoginUiEvent
}