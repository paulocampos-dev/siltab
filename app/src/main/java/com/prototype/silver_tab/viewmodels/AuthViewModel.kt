package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.workers.TokenRefreshWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = SilverTabApplication.authRepository

    // Expose the auth state
    val authState = authRepository.authState

    // Derived states for convenience
    val isAuthenticated = authState.map { it.isAuthenticated }
    val isRefreshing = MutableStateFlow(false)

    init {
        // Initialize auth repository when view model is created
        viewModelScope.launch {
            authRepository.initialize()
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            authRepository.login(username, password)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun refreshToken() {
        viewModelScope.launch {
            isRefreshing.value = true
            try {
                authRepository.refreshToken()
            } finally {
                isRefreshing.value = false
            }
        }
    }

    fun setAuthenticated(value: Boolean) {
        // This is just a stub method to maintain compatibility with your existing code
        // The actual authentication state is now managed by the repository
    }
}