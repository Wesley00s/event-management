package com.example.wedding_planner.data.repo

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.wedding_planner.data.model.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val WEB_CLIENT_ID =
        "331134203540-6tnkb9t1i0ojlrgpquorfu6bao39lggp.apps.googleusercontent.com"

    val currentUser get() = auth.currentUser

    suspend fun googleSignIn(context: Context): Result<Boolean> {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(true)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .setNonce(generateNonce())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            when (val credential = result.credential) {
                is GoogleIdTokenCredential -> {
                    val idToken = credential.idToken
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = auth.signInWithCredential(firebaseCredential).await()
                    val firebaseUser = authResult.user
                    if (firebaseUser != null) {
                        saveUserToFirestore(firebaseUser)
                    }
                    return Result.success(true)
                }

                is CustomCredential if credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        val authResult = auth.signInWithCredential(firebaseCredential).await()
                        val firebaseUser = authResult.user
                        if (firebaseUser != null) {
                            saveUserToFirestore(firebaseUser)
                        }
                        return Result.success(true)
                    } catch (e: Exception) {
                        return Result.failure(Exception("Erro ao extrair CustomCredential: ${e.message}"))
                    }
                }
                else -> {
                    val type = credential.javaClass.simpleName
                    val customType = if (credential is CustomCredential) credential.type else "N/A"
                    return Result.failure(Exception("Tipo desconhecido: $type / $customType"))
                }
            }
        } catch (e: GetCredentialException) {
            val errorType = e::class.simpleName
            val errorMessage = e.message
            return Result.failure(Exception("$errorType: $errorMessage"))
        } catch (e: Exception) {
            return Result.failure(Exception("GENERIC: ${e.localizedMessage}"))
        }
    }

    private suspend fun saveUserToFirestore(firebaseUser: com.google.firebase.auth.FirebaseUser) {
        val user = User(
            uid = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            photoUrl = firebaseUser.photoUrl?.toString()
        )

        firestore.collection("users")
            .document(firebaseUser.uid)
            .set(user, SetOptions.merge())
            .await()
    }

    fun signOut() {
        auth.signOut()
    }

    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}