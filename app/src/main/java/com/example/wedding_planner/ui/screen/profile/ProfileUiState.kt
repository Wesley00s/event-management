package com.example.wedding_planner.ui.screen.profile

data class ProfileUiState (
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val isLoggedOut: Boolean = false,

    val eventName: String = "",
    val eventDate: Long? = null,
    val partnerName: String? = null,
    val partnerPhotoUrl: String? = null
)