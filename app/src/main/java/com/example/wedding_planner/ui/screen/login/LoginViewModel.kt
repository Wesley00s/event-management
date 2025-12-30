package com.example.wedding_planner.ui.screen.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wedding_planner.data.model.User
import com.example.wedding_planner.data.model.enums.LoginDestination
import com.example.wedding_planner.data.repo.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginUiEvent) {
        viewModelScope.launch {
            when (event) {
                is LoginUiEvent.PerformLogin -> handleGoogleLogin(event.context)
            }
        }
    }

    private suspend fun handleGoogleLogin(context: Context) {
        _uiState.update { it.copy(isLoading = true) }
        try {
            authRepository.googleSignIn(context)
                .onSuccess {
                    _uiState.update { it.copy(isSuccessLogin = true, isLoading = false) }
                    checkUserDestination()
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(isLoading = false, error = e.message)
            }
        }
    }

    private suspend fun checkUserDestination() {
        val userId = authRepository.currentUser?.uid ?: return

        try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java)
            val orgId = user?.currentOrganizationId

            if (!orgId.isNullOrBlank() && orgId != "null") {
                _uiState.update {
                    it.copy(isLoading = false, navigationDestination = LoginDestination.HOME)
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigationDestination = LoginDestination.ORGANIZATION_SELECTION
                    )
                }
            }
        } catch (_: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    navigationDestination = LoginDestination.ORGANIZATION_SELECTION
                )
            }
        }
    }

    fun onNavigated() {
        _uiState.update { it.copy(navigationDestination = null) }
    }
}