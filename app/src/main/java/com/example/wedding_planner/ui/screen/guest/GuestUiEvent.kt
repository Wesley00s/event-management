package com.example.wedding_planner.ui.screen.guest

import com.example.wedding_planner.data.model.Guest
import com.example.wedding_planner.data.model.enums.SpecialRole

sealed interface GuestUiEvent {
    data class SaveGuest(val guest: Guest) : GuestUiEvent
    data class DeleteGuest(val guestId: String) : GuestUiEvent
    object Reload : GuestUiEvent
    data class UpdateRole(val guestId: String, val role: SpecialRole) : GuestUiEvent
}