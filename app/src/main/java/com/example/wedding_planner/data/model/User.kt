package com.example.wedding_planner.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val currentOrganizationId: String? = null,
    val pendingInvites: List<String> = emptyList()
)