package com.example.wedding_planner.ui.screen.settings

sealed interface SettingsUiEvent {
    data class UpdateName(val newName: String) : SettingsUiEvent
    data class UpdateLocation(val newLocation: String) : SettingsUiEvent
    data class UpdateDate(val newDate: Long) : SettingsUiEvent
    data class RemovePartner(val partnerId: String) : SettingsUiEvent
    object ClearMessages : SettingsUiEvent
}