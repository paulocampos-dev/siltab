package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val username: StateFlow<String> = authRepository.authState
        .map { it.username ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val email: StateFlow<String> = authRepository.authState
        .map { it.email ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val roleName: StateFlow<String> = authRepository.authState
        .map { it.roleName ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val positionName: StateFlow<String> = authRepository.authState
        .map { it.positionName ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val entityAuthority: StateFlow<String> = authRepository.authState
        .map { it.userEntityAuthority ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val commercialPolicyAccess: StateFlow<String> = authRepository.authState
        .map { it.hasCommercialPolicyAccess ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
}