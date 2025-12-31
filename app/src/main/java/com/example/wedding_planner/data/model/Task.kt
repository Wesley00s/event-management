package com.example.wedding_planner.data.model

import com.example.wedding_planner.data.model.enums.TaskCategory
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Task(
    @DocumentId
    val id: String = "",
    val title: String = "",
    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,
    val category: TaskCategory = TaskCategory.GENERAL,
    val createdAt: Long = System.currentTimeMillis()
)
