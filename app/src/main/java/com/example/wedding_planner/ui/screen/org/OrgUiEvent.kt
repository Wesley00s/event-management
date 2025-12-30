package com.example.wedding_planner.ui.screen.org

sealed interface OrgUiEvent {
    data class CreateOrganization(val name: String): OrgUiEvent
    data class JoinOrganization(val code: String): OrgUiEvent
    data class SelectOrganization(val orgId: String): OrgUiEvent
    data object ClearError : OrgUiEvent
    data object ResetSuccessState : OrgUiEvent
}