package com.example.wedding_planner.data.model

import com.example.wedding_planner.data.model.enums.TaskCategory
import com.google.firebase.firestore.DocumentId

data class Task(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val isCompleted: Boolean = false,
    val category: TaskCategory = TaskCategory.GENERAL,
    val createdAt: Long = System.currentTimeMillis()
)
