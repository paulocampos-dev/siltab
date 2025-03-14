package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.session.AppSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopBarViewModel @Inject constructor(
    private val sessionManager : AppSessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    val selectedDealer = sessionManager.selectedDealer

    fun logout(){
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}