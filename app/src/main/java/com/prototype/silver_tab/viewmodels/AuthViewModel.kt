package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.SilverTabApplication.Companion.tokenManager
import com.prototype.silver_tab.data.api.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

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

    fun setAuthenticated(value: Boolean) {
        _isAuthenticated.value = value
    }
}