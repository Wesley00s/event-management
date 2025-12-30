package com.example.wedding_planner.ui.screen.settings

import com.example.wedding_planner.data.model.User
import com.example.wedding_planner.data.model.WeddingOrganization

data class SettingsUiState(
    val isLoading: Boolean = false,
    val organization: WeddingOrganization? = null,
    val participants: List<User> = emptyList(),
    val currentUserId: String = "",
    val error: String? = null,
    val successMessage: String? = null
)