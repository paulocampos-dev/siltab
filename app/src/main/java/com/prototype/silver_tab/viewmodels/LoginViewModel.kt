package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class UserRole {
    ADMIN,
    BASIC_USER,
    NONE
}

data class LoginState(
    val username: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val userRole: UserRole = UserRole.NONE
)

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    // Hardcoded credentials for demo
    private val credentials = mapOf(
        "a" to Pair("a", UserRole.ADMIN),
        "level2_regionalmanager" to Pair("Test#154", UserRole.ADMIN),
        "BYDAMEBR0015W_U" to Pair("15Da17@02", UserRole.BASIC_USER),
        "b" to Pair("b", UserRole.BASIC_USER)
    )

    fun updateUsername(username: String) {
        _state.update { it.copy(username = username, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _state.update { it.copy(password = password, errorMessage = null) }
    }

    fun attemptLogin(): Boolean {
        val userCredentials = credentials[state.value.username]

        return if (userCredentials?.first == state.value.password) {
            _state.update {
                it.copy(
                    errorMessage = null,
                    userRole = userCredentials.second
                )
            }
            true
        } else {
            _state.update {
                it.copy(
                    errorMessage = "Email ou senha inválidos",
                    userRole = UserRole.NONE
                )
            }
            false
        }
    }

    fun getUserRole(): UserRole {
        return state.value.userRole
    }
}