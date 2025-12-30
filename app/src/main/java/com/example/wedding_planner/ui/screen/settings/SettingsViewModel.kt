package com.example.wedding_planner.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wedding_planner.data.repo.AuthRepository
import com.example.wedding_planner.data.repo.OrganizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: OrganizationRepository,
    authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val currentUserId = authRepo.currentUser?.uid ?: ""
        _uiState.update { it.copy(currentUserId = currentUserId) }
        loadCurrentOrganization()
    }

    fun onEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.UpdateName -> updateName(event.newName)
            is SettingsUiEvent.RemovePartner -> removePartner(event.partnerId)
            is SettingsUiEvent.ClearMessages -> _uiState.update {
                it.copy(
                    error = null,
                    successMessage = null
                )
            }

            is SettingsUiEvent.UpdateDate -> updateDate(event.newDate)
            is SettingsUiEvent.UpdateLocation -> updateLocation(event.newLocation)
        }
    }

    private fun loadCurrentOrganization() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repo.getCurrentOrganization()
                .onSuccess { org ->
                    _uiState.update { it.copy(organization = org) }

                    if (org != null && org.members.isNotEmpty()) {
                        loadParticipants(org.members)
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun loadParticipants(memberIds: List<String>) {
        viewModelScope.launch {
            repo.getUsersByIds(memberIds)
                .onSuccess { users ->
                    _uiState.update { it.copy(isLoading = false, participants = users) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun removePartner(partnerId: String) {
        val org = _uiState.value.organization ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repo.removeMember(org.id, partnerId)
                .onSuccess {
                    loadCurrentOrganization()
                    _uiState.update { it.copy(successMessage = "Parceiro removido com sucesso") }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Erro ao remover parceiro: ${e.message}"
                        )
                    }
                }
        }
    }

    private fun updateName(newName: String) {
        val currentOrg = _uiState.value.organization ?: return
        if (newName.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repo.updateOrganizationName(currentOrg.id, newName)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Nome atualizado com sucesso",
                            organization = it.organization?.copy(name = newName)
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Erro ao atualizar nome: ${e.message}"
                        )
                    }
                }
        }
    }

    private fun updateLocation(newLocation: String) {
        val currentOrg = _uiState.value.organization ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repo.updateOrganizationLocation(currentOrg.id, newLocation)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Localização atualizada com sucesso",
                            organization = it.organization?.copy(location = newLocation)
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Erro ao atualizar local: ${e.message}"
                        )
                    }
                }
        }
    }

    private fun updateDate(newDate: Long) {
        val currentOrg = _uiState.value.organization ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repo.updateOrganizationDate(currentOrg.id, newDate)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Data atualizada com sucesso",
                            organization = it.organization?.copy(date = newDate)
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Erro ao atualizar data: ${e.message}"
                        )
                    }
                }
        }
    }
}