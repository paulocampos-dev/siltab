package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.api.AuthManager
import com.prototype.silver_tab.data.api.RetrofitClient
import com.prototype.silver_tab.data.models.auth.AuthResult
import com.prototype.silver_tab.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository(RetrofitClient.authApi)

    private val _loginState = MutableStateFlow<AuthResult<*>?>(null)
    val loginState: StateFlow<AuthResult<*>?> = _loginState.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun updateUsername(value: String) {
        _username.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun login() {
        viewModelScope.launch {
            println("Attempting login with username: ${_username.value}")
            authRepository.login(_username.value, _password.value)
                .collect { result ->
                    println("Login result: $result")
                    _loginState.value = result
                    if (result is AuthResult.Success) {
                        // Store tokens
                        val response = result.data
                        AuthManager.setTokens(
                            response.accessToken,
                            response.refreshToken
                        )
                    }
                }
        }
    }

    fun clearLoginState() {
        _loginState.value = null
    }
}