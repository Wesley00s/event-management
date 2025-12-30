package com.example.wedding_planner.data.model

import com.example.wedding_planner.data.model.enums.BudgetCategory
import com.google.firebase.firestore.DocumentId

data class Expense(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val category: BudgetCategory = BudgetCategory.GENERAL,
    val estimatedCost: Double = 0.0,
    val actualCost: Double = 0.0,
    val paidAmount: Double = 0.0,
    val dueDate: Long? = null        
) {
    val isFullyPaid: Boolean get() = paidAmount >= actualCost && actualCost > 0
    val remainingToPay: Double get() = actualCost - paidAmount
}