package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val tag: String = "LoginViewModel"


    val loginState = authRepository.authState

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    init {
        logTimber(tag, "LoginViewModel initialized")
        clearLoginState()
    }

    fun updateUsername(value: String) {
        _username.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun login() {

        logTimber(tag, "Login attempt initiated for user: ${_username.value}")

        viewModelScope.launch {
            try {
                val result = authRepository.login(_username.value, _password.value)

                if (result.isSuccess) {
                    logTimber(tag, "Login successful for user: ${_username.value}")
                } else {
                    logTimber(tag, "Login failed for user: ${_username.value}")
                }
            } catch (e: Exception) {
                logTimberError(tag, "Error during login for user: ${_username.value}")
            }
        }
    }

    fun clearLoginState() {
        Timber.d("Clearing login state")

        _username.value = ""
        _password.value = ""
    }
}