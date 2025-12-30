package com.example.wedding_planner.data.repo

import com.example.wedding_planner.data.model.User
import com.example.wedding_planner.data.model.WeddingOrganization
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class OrganizationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val userId get() = auth.currentUser?.uid ?: ""

    suspend fun getUserOrganizations(): Result<List<WeddingOrganization>> {
        return try {
            if (userId.isEmpty()) return Result.success(emptyList())

            val snapshot = firestore.collection("organizations")
                .whereArrayContains("members", userId)
                .get()
                .await()

            val orgs = snapshot.toObjects(WeddingOrganization::class.java)
            Result.success(orgs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrganization(name: String): Result<String> {
        return try {
            val orgId = UUID.randomUUID().toString()
            val accessCode = UUID.randomUUID().toString().substring(0, 6).uppercase()

            val org = WeddingOrganization(
                id = orgId,
                name = name,
                accessCode = accessCode,
                ownerId = userId,
                members = listOf(userId),
                eventDate = null
            )

            firestore.collection("organizations").document(orgId).set(org).await()

            selectOrganization(orgId)

            Result.success(orgId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinByCode(code: String): Result<String> {
        return try {
            val snapshot = firestore.collection("organizations")
                .whereEqualTo("accessCode", code.uppercase())
                .limit(1)
                .get()
                .await()

            if (snapshot.isEmpty) {
                return Result.failure(Exception("Código inválido"))
            }

            val doc = snapshot.documents.first()
            val orgId = doc.id

            firestore.collection("organizations").document(orgId)
                .update("members", FieldValue.arrayUnion(userId))
                .await()

            selectOrganization(orgId)
            Result.success(orgId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun selectOrganization(orgId: String) {
        if (userId.isNotEmpty()) {
            firestore.collection("users").document(userId)
                .set(mapOf("currentOrganizationId" to orgId), SetOptions.merge())
                .await()
        }
    }

    suspend fun getCurrentOrganization(): Result<WeddingOrganization?> {
        return try {
            if (userId.isEmpty()) return Result.failure(Exception("Usuário não logado"))

            val userDoc = firestore.collection("users").document(userId).get().await()
            val currentOrgId = userDoc.getString("currentOrganizationId")

            if (currentOrgId.isNullOrBlank()) {
                return Result.success(null)
            }

            val orgDoc = firestore.collection("organizations").document(currentOrgId).get().await()
            val org = orgDoc.toObject(WeddingOrganization::class.java)

            Result.success(org)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrganizationName(orgId: String, newName: String): Result<Unit> {
        return try {
            firestore.collection("organizations").document(orgId)
                .update("name", newName)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsersByIds(userIds: List<String>): Result<List<User>> {
        return try {
            if (userIds.isEmpty()) return Result.success(emptyList())

            val snapshot = firestore.collection("users")
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .await()

            val users = snapshot.toObjects(User::class.java)
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeMember(orgId: String, memberId: String): Result<Unit> {
        return try {
            firestore.collection("organizations").document(orgId)
                .update("members", FieldValue.arrayRemove(memberId))
                .await()

            firestore.collection("users").document(memberId).update("currentOrganizationId", null)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateOrganizationLocation(orgId: String, newLocation: String): Result<Unit> {
        return try {
            firestore.collection("organizations").document(orgId)
                .update("location", newLocation)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateOrganizationDate(id: String, newDate: Long): Result<Unit> {
        return try {
            firestore.collection("organizations").document(id)
                .update("date", newDate)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}