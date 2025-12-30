package com.example.wedding_planner.ui.screen.guest

import com.example.wedding_planner.data.model.Guest

data class GuestUiState(
    val isLoading: Boolean = false,
    val guests: List<Guest> = emptyList(),
    val totalCount: Int = 0,
    val error: String? = null
)