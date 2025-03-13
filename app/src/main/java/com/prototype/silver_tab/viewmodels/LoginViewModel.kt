package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.models.auth.LoginResponse
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.data.store.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Auth state is now provided by the repository
    val loginState = authRepository.authState

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    init {
        Timber.d("LoginViewModel initialized")
        clearLoginState()
    }

    fun updateUsername(value: String) {
        _username.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun login(dealerViewModel: DealerViewModel) {
        Timber.d("Login attempt initiated for user: ${_username.value}")
        viewModelScope.launch {
            try {
                val result = authRepository.login(_username.value, _password.value)

                if (result.isSuccess) {
                    Timber.d("Login successful for user: ${_username.value}")
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
                        userPreferences.saveUserData(loginResponse)
                        Timber.d("User data saved to preferences")
                    }

                    dealerViewModel.notifyAuthenticated()
                } else {
                    Timber.e("Login failed for user: ${_username.value}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error during login for user: ${_username.value}")
            }
        }
    }

    fun clearLoginState() {
        Timber.d("Clearing login state")
        // We don't need to manually clear state anymore as it's managed by the repository
        // Just clear the input fields
        _username.value = ""
        _password.value = ""
    }
}