package com.example.wedding_planner.ui.screen.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wedding_planner.data.model.User
import com.example.wedding_planner.data.model.enums.SplashDestination
import com.example.wedding_planner.data.repo.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkAuthAndOrganization()
    }

    private fun checkAuthAndOrganization() {
        viewModelScope.launch {
            delay(500L)
            val currentUser = auth.currentUser
            if (currentUser == null) {
                _uiState.update { it.copy(isLoading = false, destination = SplashDestination.LOGIN) }
                return@launch
            }

            try {
                val userDoc = firestore.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()

                val user = userDoc.toObject(User::class.java)
                val orgId = user?.currentOrganizationId

                if (!orgId.isNullOrBlank() && orgId != "null") {
                    _uiState.update { it.copy(isLoading = false, destination = SplashDestination.HOME) }
                } else {
                    _uiState.update { it.copy(isLoading = false, destination = SplashDestination.ORGANIZATION_SELECTION) }
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, destination = SplashDestination.ORGANIZATION_SELECTION) }
            }
        }
    }
}