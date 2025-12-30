package com.example.wedding_planner.data.model

import com.example.wedding_planner.data.model.enums.GuestSide
import com.example.wedding_planner.data.model.enums.Kinship
import com.example.wedding_planner.data.model.enums.SpecialRole
import com.google.firebase.firestore.DocumentId

data class Guest(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val side: GuestSide = GuestSide.GROOM,
    val kinship: Kinship = Kinship.FRIEND,
    val specialRole: SpecialRole? = null,
    val groupId: String? = null
) {
    fun isHonorGuest(): Boolean = specialRole != null && specialRole != SpecialRole.NONE
}