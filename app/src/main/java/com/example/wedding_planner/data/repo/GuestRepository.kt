package com.example.wedding_planner.data.repo

import com.example.wedding_planner.data.model.Guest
import com.example.wedding_planner.data.model.enums.SpecialRole
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GuestRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getGuests(orgId: String): Result<List<Guest>> {
        return try {
            val snapshot = firestore.collection("organizations")
                .document(orgId)
                .collection("guests")
                .get()
                .await()

            val guests = snapshot.toObjects(Guest::class.java)
            Result.success(guests)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveGuest(orgId: String, guest: Guest): Result<Unit> {
        return try {
            val collection = firestore.collection("organizations")
                .document(orgId)
                .collection("guests")

            val docRef = if (guest.id.isNotEmpty()) {
                collection.document(guest.id)
            } else {
                collection.document()
            }
            val guestToSave = guest.copy(id = docRef.id)
            docRef.set(guestToSave, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteGuest(orgId: String, guestId: String): Result<Unit> {
        return try {
            firestore.collection("organizations")
                .document(orgId)
                .collection("guests")
                .document(guestId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSpecialRole(orgId: String, guestId: String, role: SpecialRole): Result<Unit> {
        return try {
            firestore.collection("organizations")
                .document(orgId)
                .collection("guests")
                .document(guestId)
                .update("specialRole", role)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}