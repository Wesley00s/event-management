package com.example.wedding_planner.ui.screen.main

import androidx.lifecycle.ViewModel
import com.example.wedding_planner.data.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {

    val photoUrl: String? = authRepository.currentUser?.photoUrl?.toString()
}