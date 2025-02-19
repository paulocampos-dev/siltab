package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.SilverTabApplication.Companion.tokenManager
import com.prototype.silver_tab.data.api.AuthManager
import com.prototype.silver_tab.data.api.RetrofitClient
import com.prototype.silver_tab.data.models.auth.AuthResult
import com.prototype.silver_tab.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val accessToken = tokenManager.getAccessToken()
            if (!accessToken.isNullOrEmpty()) {
                AuthManager.setTokens(
                    accessToken = accessToken,
                    refreshToken = tokenManager.getRefreshToken() ?: ""
                )
                _isAuthenticated.value = true
            }
        }
    }

    fun refreshToken() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val refreshToken = tokenManager.getRefreshToken()
                if (refreshToken.isNullOrEmpty()) {
                    _isAuthenticated.value = false
                    return@launch
                }

                val authRepository = AuthRepository(RetrofitClient.authApi)
                authRepository.refreshToken(refreshToken).collect { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            val response = result.data
                            AuthManager.setTokens(
                                accessToken = response.accessToken,
                                refreshToken = response.refreshToken
                            )
                            tokenManager.saveTokens(
                                response.accessToken,
                                response.refreshToken
                            )
                            _isAuthenticated.value = true
                        }
                        is AuthResult.Error -> {
                            _isAuthenticated.value = false
                        }
                        is AuthResult.Loading -> {
                            // Do nothing while loading
                        }
                    }
                }
            } catch (e: Exception) {
                _isAuthenticated.value = false
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun setAuthenticated(value: Boolean) {
        _isAuthenticated.value = value
    }
}