package com.prototype.silver_tab.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.data.models.auth.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authRepository = SilverTabApplication.authRepository

    // Auth state is now provided by the repository
    val loginState = authRepository.authState

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    init {
        clearLoginState()
    }

    fun updateUsername(value: String) {
        _username.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun login(dealerViewModel: DealerViewModel) {
        viewModelScope.launch {
            try {
                val result = authRepository.login(_username.value, _password.value)

                if (result.isSuccess) {
                    // Get current state with user info
                    val currentState = authRepository.authState.value

                    // Create LoginResponse object to save in UserPreferences
                    if (currentState.isAuthenticated && currentState.username != null) {
                        val loginResponse = LoginResponse(
                            accessToken = currentState.accessToken ?: "",
                            refreshToken = currentState.refreshToken ?: "",
                            username = currentState.username ?: "",
                            email = currentState.email ?: "",
                            role = currentState.role ?: 0,
                            roleName = currentState.roleName ?: "",
                            position = currentState.position ?: 0L,
                            positionName = currentState.positionName ?: "",
                            id = currentState.userId ?: 0L,
                            userEntityAuthority = currentState.userEntityAuthority ?: "",
                            userHasAccessToCommercialPolicy = currentState.hasCommercialPolicyAccess ?: "",
                            tokenType = "Bearer"
                        )

                        // Save to UserPreferences
                        SilverTabApplication.userPreferences.saveUserData(loginResponse)
                    }

                    dealerViewModel.notifyAuthenticated()
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error during login: ${e.message}")
            }
        }
    }

    fun clearLoginState() {
        // We don't need to manually clear state anymore as it's managed by the repository
        // Just clear the input fields
        _username.value = ""
        _password.value = ""
    }
}