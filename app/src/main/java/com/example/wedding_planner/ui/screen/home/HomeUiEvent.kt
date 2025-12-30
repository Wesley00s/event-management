package com.example.wedding_planner.ui.screen.home

sealed interface HomeUiEvent {
    data object Refresh : HomeUiEvent
}