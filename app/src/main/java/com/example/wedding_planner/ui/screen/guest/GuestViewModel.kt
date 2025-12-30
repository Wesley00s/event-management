package com.example.wedding_planner.ui.screen.guest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wedding_planner.data.model.enums.SpecialRole
import com.example.wedding_planner.data.repo.GuestRepository
import com.example.wedding_planner.data.repo.OrganizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuestViewModel @Inject constructor(
    private val guestRepository: GuestRepository,
    private val orgRepository: OrganizationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuestUiState())
    val uiState = _uiState.asStateFlow()
    
    private var currentOrgId: String? = null

    init {
        loadGuests()
    }

    fun onEvent(event: GuestUiEvent) {
        when (event) {
            is GuestUiEvent.SaveGuest -> saveGuest(event.guest)
            is GuestUiEvent.DeleteGuest -> deleteGuest(event.guestId)
            is GuestUiEvent.Reload -> loadGuests()
            is GuestUiEvent.UpdateRole -> updateRole(event.guestId, event.role)
        }
    }

    private fun loadGuests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            if (currentOrgId == null) {
                val orgResult = orgRepository.getCurrentOrganization()
                currentOrgId = orgResult.getOrNull()?.id
            }
            val orgId = currentOrgId ?: return@launch

            guestRepository.getGuests(orgId)
                .onSuccess { guests ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            guests = guests,
                            totalCount = guests.size
                        ) 
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun saveGuest(guest: com.example.wedding_planner.data.model.Guest) {
        val orgId = currentOrgId ?: return
        viewModelScope.launch {
            guestRepository.saveGuest(orgId, guest)
                .onSuccess {
                    loadGuests()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    private fun updateRole(guestId: String, role: SpecialRole) {
        val orgId = currentOrgId ?: return
        _uiState.update { state ->
            val updatedList = state.guests.map {
                if (it.id == guestId) it.copy(specialRole = role) else it
            }
            state.copy(guests = updatedList)
        }
        viewModelScope.launch {
            guestRepository.updateSpecialRole(orgId, guestId, role).onFailure { loadGuests() }
        }
    }

    private fun deleteGuest(guestId: String) {
        val orgId = currentOrgId ?: return
        viewModelScope.launch {
            guestRepository.deleteGuest(orgId, guestId).onSuccess { loadGuests() }
        }
    }

}