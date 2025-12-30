package com.example.wedding_planner.data.repo

import com.example.wedding_planner.data.model.Expense
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BudgetRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getExpenses(orgId: String): Result<List<Expense>> {
        return try {
            val snapshot = firestore.collection("organizations")
                .document(orgId)
                .collection("expenses")
                .get()
                .await()
            Result.success(snapshot.toObjects(Expense::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveExpense(orgId: String, expense: Expense): Result<Unit> {
        return try {
            val collection = firestore.collection("organizations").document(orgId).collection("expenses")
            val docRef = if (expense.id.isNotEmpty()) collection.document(expense.id) else collection.document()
            
            val expenseToSave = expense.copy(id = docRef.id)
            docRef.set(expenseToSave, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteExpense(orgId: String, expenseId: String): Result<Unit> {
        return try {
            firestore.collection("organizations")
                .document(orgId)
                .collection("expenses")
                .document(expenseId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBudgetLimit(orgId: String, limit: Double): Result<Unit> {
        return try {
            firestore.collection("organizations")
                .document(orgId)
                .update("budgetLimit", limit)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}