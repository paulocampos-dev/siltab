package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.workers.TokenRefreshWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Expose the auth state
    val authState = authRepository.authState

    // Derived states for convenience
    val isAuthenticated = authState.map { it.isAuthenticated }
    val isRefreshing = MutableStateFlow(false)

    init {
        Timber.d("AuthViewModel initialized")
        // Initialize auth repository when view model is created
        viewModelScope.launch {
            try {
                authRepository.initialize()
                Timber.d("Auth repository initialized")
            } catch (e: Exception) {
                Timber.e(e, "Error initializing auth repository")
            }
        }
    }

    fun login(username: String, password: String) {
        Timber.d("Login attempt for user: $username")
        viewModelScope.launch {
            try {
                authRepository.login(username, password)
                Timber.d("Login complete for user: $username")
            } catch (e: Exception) {
                Timber.e(e, "Login error for user: $username")
            }
        }
    }

    fun logout() {
        Timber.d("Logout initiated")
        viewModelScope.launch {
            try {
                authRepository.logout()
                Timber.d("Logout completed")
            } catch (e: Exception) {
                Timber.e(e, "Error during logout")
            }
        }
    }

    fun refreshToken() {
        Timber.d("Token refresh initiated")
        viewModelScope.launch {
            isRefreshing.value = true
            try {
                val result = authRepository.refreshToken()
                if (result.isSuccess) {
                    Timber.d("Token refresh successful")
                } else {
                    Timber.w("Token refresh failed: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error during token refresh")
            } finally {
                isRefreshing.value = false
            }
        }
    }
}