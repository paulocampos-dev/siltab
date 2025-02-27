package com.prototype.silver_tab.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.SilverTabApplication
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

    private val userPreferences = SilverTabApplication.userPreferences

    init {
        // Remove the token clearing from init
        clearLoginState()
    }

    fun updateUsername(value: String) {
        _username.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun login() {
        viewModelScope.launch {
            try {
                println("Attempting login with username: ${_username.value}")
                authRepository.login(_username.value, _password.value)
                    .collect { result ->
                        println("Login result: $result")
                        _loginState.value = result
                        if (result is AuthResult.Success) {
                            val response = result.data
                            // Store tokens in both AuthManager and TokenManager
                            AuthManager.setTokens(
                                response.accessToken,
                                response.refreshToken
                            )
                            // Store tokens persistently
                            SilverTabApplication.tokenManager.saveTokens(
                                response.accessToken,
                                response.refreshToken
                            )

                            // Store user data
                            userPreferences.saveUserData(response)
                            AuthManager.getRefreshToken()
                        }
                    }
            } catch (e: Exception) {
                Log.e("com.prototype.silver_tab.viewmodels.LoginViewModel", "Error during login: ${e.message}")
            }
        }
    }

    fun clearLoginState() {
        _loginState.value = null
    }
}