package com.example.wedding_planner.data.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class WeddingOrganization(
    @DocumentId
    val id: String = "",
    val name: String = "", 
    val accessCode: String = "",
    val eventDate: Date? = null,
    val ownerId: String = "",
    val members: List<String> = emptyList(),
    val date: Long? = null,
    val location: String? = null,
    val budgetLimit: Double? = null
)
