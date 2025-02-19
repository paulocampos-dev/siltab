package com.prototype.silver_tab.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.SilverTabApplication.Companion.userPreferences
import com.prototype.silver_tab.data.api.AuthManager
import com.prototype.silver_tab.data.api.RetrofitClient
import com.prototype.silver_tab.data.models.auth.AuthResult
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.data.store.UserPreferences
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
        viewModelScope.launch {
            try {
                AuthManager.clearTokens()
                userPreferences.clearUserData()
                clearLoginState()
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error during initialization: ${e.message}")
            }
        }
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
                            // Store tokens
                            AuthManager.setTokens(
                                response.accessToken,
                                response.refreshToken
                            )
                            // Store user data
                            userPreferences.saveUserData(response)
                        }
                    }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error during login: ${e.message}")
                //_loginState.value = AuthResult.Error("Login failed: ${e.message}")
            }
        }
    }

    fun clearLoginState() {
        _loginState.value = null
    }
}