package com.example.wedding_planner.data.repo

import com.example.wedding_planner.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getTasks(orgId: String): Result<List<Task>> {
        return try {
            val snapshot = firestore.collection("organizations")
                .document(orgId)
                .collection("tasks")
                .orderBy("createdAt")
                .get()
                .await()
            Result.success(snapshot.toObjects(Task::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveTask(orgId: String, task: Task): Result<Unit> {
        return try {
            val collection = firestore.collection("organizations").document(orgId).collection("tasks")
            val docRef = if (task.id.isNotEmpty()) collection.document(task.id) else collection.document()
            
            val taskToSave = task.copy(id = docRef.id)
            docRef.set(taskToSave, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleTaskStatus(orgId: String, taskId: String, isCompleted: Boolean): Result<Unit> {
        return try {
            firestore.collection("organizations")
                .document(orgId)
                .collection("tasks")
                .document(taskId)
                .update("isCompleted", isCompleted)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(orgId: String, taskId: String): Result<Unit> {
        return try {
            firestore.collection("organizations")
                .document(orgId)
                .collection("tasks")
                .document(taskId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}