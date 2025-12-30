package com.example.wedding_planner.data.model

import com.example.wedding_planner.data.model.enums.ActionType
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class ActivityLog(
    @DocumentId
    val id: String = "",
    val actorName: String = "",
    val message: String = "",
    val timestamp: Date = Date(),
    val actionType: ActionType = ActionType.GENERAL,
    val targetId: String? = null 
)
