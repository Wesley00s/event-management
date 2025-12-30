package com.example.wedding_planner.ui.screen.org

import com.example.wedding_planner.data.model.WeddingOrganization

data class OrgUiState(
    val isLoading: Boolean = false,
    val organizations: List<WeddingOrganization> = emptyList(),
    val currentSelectedId: String? = null,
    val error: String? = null,
    val successMessage: String? = null,
    val isOperationSuccess : Boolean = false
)