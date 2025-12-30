package com.example.wedding_planner.ui.screen.org

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wedding_planner.data.repo.OrganizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrgViewModel @Inject constructor(
    private val orgRepository: OrganizationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrgUiState())
    val uiState: StateFlow<OrgUiState> = _uiState.asStateFlow()

    init {
        loadOrganizations()
    }

    fun onEvent(event: OrgUiEvent) {
        when (event) {

            is OrgUiEvent.CreateOrganization -> createOrganization(event.name)
            is OrgUiEvent.JoinOrganization -> joinOrganization(event.code)
            is OrgUiEvent.SelectOrganization -> selectOrganization(event.orgId)

            is OrgUiEvent.ClearError -> clearError()
            is OrgUiEvent.ResetSuccessState -> resetSuccessState()
        }
    }

    private fun loadOrganizations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = orgRepository.getUserOrganizations()

            result.onSuccess { orgs ->
                _uiState.update { it.copy(isLoading = false, organizations = orgs) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun createOrganization(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            orgRepository.createOrganization(name)
                .onSuccess {
                    loadOrganizations()
                    _uiState.update { it.copy(isLoading = false, isOperationSuccess = true) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = "Erro ao criar: ${e.message}")
                    }
                }
        }
    }

    private fun joinOrganization(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            orgRepository.joinByCode(code)
                .onSuccess {
                    loadOrganizations()
                    _uiState.update { it.copy(isLoading = false, isOperationSuccess = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun selectOrganization(orgId: String) {
        viewModelScope.launch {
            orgRepository.selectOrganization(orgId)
            _uiState.update { it.copy(isOperationSuccess = true) }
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun resetSuccessState() {
        _uiState.update { it.copy(isOperationSuccess = false) }
    }
}