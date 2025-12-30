package com.example.wedding_planner.ui.screen.profile

sealed interface ProfileUiEvent {
    data object PerformLogout : ProfileUiEvent
}