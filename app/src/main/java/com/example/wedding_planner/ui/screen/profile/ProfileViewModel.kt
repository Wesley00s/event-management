package com.example.wedding_planner.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wedding_planner.data.repo.AuthRepository
import com.example.wedding_planner.data.repo.OrganizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val orgRepository: OrganizationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState : StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.PerformLogout -> {
                authRepository.signOut()
                _uiState.value = _uiState.value.copy(isLoggedOut = true)
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.currentUser
            if (user != null) {
                // 1. Dados Básicos
                _uiState.update {
                    it.copy(
                        name = user.displayName ?: "Usuário",
                        email = user.email ?: "",
                        photoUrl = user.photoUrl?.toString()
                    )
                }

                // 2. Buscar Dados do Casamento e Parceiro
                orgRepository.getCurrentOrganization().onSuccess { org ->
                    if (org != null) {
                        // Achar o parceiro (quem está na lista de membros mas não sou eu)
                        val partnerId = org.members.firstOrNull { it != user.uid }

                        var pName: String? = null
                        var pPhoto: String? = null

                        if (partnerId != null) {
                            // Busca dados do parceiro no banco
                            val partners = orgRepository.getUsersByIds(listOf(partnerId)).getOrNull()
                            val partnerUser = partners?.firstOrNull()
                            pName = partnerUser?.name
                            pPhoto = partnerUser?.photoUrl
                        }

                        _uiState.update {
                            it.copy(
                                eventName = org.name,
                                eventDate = org.date,
                                partnerName = pName,
                                partnerPhotoUrl = pPhoto
                            )
                        }
                    }
                }
            }
        }
    }
}