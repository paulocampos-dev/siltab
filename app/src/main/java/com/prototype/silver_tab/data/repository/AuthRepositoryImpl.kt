package com.prototype.silver_tab.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import com.prototype.silver_tab.data.api_connection.routes.AuthRoutes
import com.prototype.silver_tab.data.models.auth.AuthState
import com.prototype.silver_tab.data.models.auth.LoginRequest
import com.prototype.silver_tab.data.models.auth.LoginResponse
import com.prototype.silver_tab.data.models.auth.RefreshTokenRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val context: Context,
    private val authRoutes: AuthRoutes,
    private val encryptedPreferences: SharedPreferences
) : AuthRepository {
    private val _authState = MutableStateFlow(AuthState())
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Keys for encrypted storage
    private companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val USER_ID_KEY = "user_id"
        private const val USERNAME_KEY = "username"
        private const val EMAIL_KEY = "email"
        private const val ROLE_KEY = "role"
        private const val ROLE_NAME_KEY = "role_name"
        private const val POSITION_KEY = "position"
        private const val POSITION_NAME_KEY = "position_name"
        private const val USER_ENTITY_AUTHORITY_KEY = "user_entity_authority"
        private const val COMMERCIAL_POLICY_ACCESS_KEY = "commercial_policy_access"
    }

    override suspend fun initialize() {
        // Load stored auth data on initialization
        withContext(Dispatchers.IO) {
            try {
                val accessToken = encryptedPreferences.getString(ACCESS_TOKEN_KEY, null)
                val refreshToken = encryptedPreferences.getString(REFRESH_TOKEN_KEY, null)

                if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                    // Load all stored user data
                    val userId = encryptedPreferences.getLong(USER_ID_KEY, 0L)
                    val username = encryptedPreferences.getString(USERNAME_KEY, null)
                    val email = encryptedPreferences.getString(EMAIL_KEY, null)
                    val role = encryptedPreferences.getInt(ROLE_KEY, 0)
                    val roleName = encryptedPreferences.getString(ROLE_NAME_KEY, null)
                    val position = encryptedPreferences.getLong(POSITION_KEY, 0L)
                    val positionName = encryptedPreferences.getString(POSITION_NAME_KEY, null)
                    val userEntityAuthority = encryptedPreferences.getString(USER_ENTITY_AUTHORITY_KEY, null)
                    val hasCommercialPolicyAccess = encryptedPreferences.getString(COMMERCIAL_POLICY_ACCESS_KEY, null)

                    _authState.update { currentState ->
                        currentState.copy(
                            isAuthenticated = true,
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            userId = userId.takeIf { it > 0L },
                            username = username,
                            email = email,
                            role = role.takeIf { it > 0 },
                            roleName = roleName,
                            position = position.takeIf { it > 0L },
                            positionName = positionName,
                            userEntityAuthority = userEntityAuthority,
                            hasCommercialPolicyAccess = hasCommercialPolicyAccess
                        )
                    }
                } else {
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error initializing auth state", e)
                // We don't update state - it stays as default (not authenticated)
            }
        }
    }

    override suspend fun login(username: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                _authState.update { it.copy(isLoading = true, error = null) }

                val loginRequest = LoginRequest(username, password)
                val response = authRoutes.login(loginRequest)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        saveAuthData(loginResponse)
                        Result.success(Unit)
                    } else {
                        _authState.update { it.copy(isLoading = false, error = "Empty response") }
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    val errorMessage = "Login failed: ${response.message()}"
                    _authState.update { it.copy(isLoading = false, error = errorMessage) }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                val errorMessage = "Error during login: ${e.message}"
                _authState.update { it.copy(isLoading = false, error = errorMessage) }
                Result.failure(e)
            }
        }
    }

    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            try {
                // Clear stored tokens
                encryptedPreferences.edit().clear().apply()

                // Reset state
                _authState.update { AuthState() }
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error during logout", e)
                // Even if there's an error, we reset the auth state
                _authState.update { AuthState() }
            }
        }
    }

    override suspend fun refreshToken(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val currentRefreshToken = _authState.value.refreshToken

                if (currentRefreshToken.isNullOrEmpty()) {
                    _authState.update { it.copy(isAuthenticated = false, error = "No refresh token available") }
                    return@withContext Result.failure(Exception("No refresh token available"))
                }

                _authState.update { it.copy(isLoading = true, error = null) }

                val refreshRequest = RefreshTokenRequest(currentRefreshToken)
                val response = authRoutes.refreshToken(refreshRequest)

                if (response.isSuccessful) {
                    val refreshResponse = response.body()
                    if (refreshResponse != null) {
                        // Update only the tokens
                        saveTokens(refreshResponse.accessToken, refreshResponse.refreshToken)
                        Result.success(Unit)
                    } else {
                        _authState.update { it.copy(isLoading = false, error = "Empty refresh response") }
                        Result.failure(Exception("Empty refresh response body"))
                    }
                } else {
                    // If refresh fails, we consider the user no longer authenticated
                    val errorMessage = "Token refresh failed: ${response.message()}"
                    _authState.update {
                        AuthState(error = errorMessage)
                    }
                    // Clear preferences
                    encryptedPreferences.edit().clear().apply()
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                // If refresh throws an exception, user is no longer authenticated
                _authState.update { AuthState(error = "Refresh error: ${e.message}") }
                encryptedPreferences.edit().clear().apply()
                Result.failure(e)
            }
        }
    }

    override suspend fun getAccessToken(): String? {
        // Check if token might be expired (you may need to add token expiration check logic)
        val currentState = _authState.value
        return if (currentState.isAuthenticated) {
            currentState.accessToken
        } else {
            null
        }
    }

    private suspend fun saveAuthData(loginResponse: LoginResponse) {
        withContext(Dispatchers.IO) {
            encryptedPreferences.edit().apply {
                putString(ACCESS_TOKEN_KEY, loginResponse.accessToken)
                putString(REFRESH_TOKEN_KEY, loginResponse.refreshToken)
                putLong(USER_ID_KEY, loginResponse.id)
                putString(USERNAME_KEY, loginResponse.username)
                putString(EMAIL_KEY, loginResponse.email)
                putInt(ROLE_KEY, loginResponse.role)
                putString(ROLE_NAME_KEY, loginResponse.roleName)
                putLong(POSITION_KEY, loginResponse.position)
                putString(POSITION_NAME_KEY, loginResponse.positionName)
                putString(USER_ENTITY_AUTHORITY_KEY, loginResponse.userEntityAuthority)
                putString(COMMERCIAL_POLICY_ACCESS_KEY, loginResponse.userHasAccessToCommercialPolicy)
            }.apply()

            _authState.update { currentState ->
                currentState.copy(
                    isAuthenticated = true,
                    isLoading = false,
                    error = null,
                    accessToken = loginResponse.accessToken,
                    refreshToken = loginResponse.refreshToken,
                    userId = loginResponse.id,
                    username = loginResponse.username,
                    email = loginResponse.email,
                    role = loginResponse.role,
                    roleName = loginResponse.roleName,
                    position = loginResponse.position,
                    positionName = loginResponse.positionName,
                    userEntityAuthority = loginResponse.userEntityAuthority,
                    hasCommercialPolicyAccess = loginResponse.userHasAccessToCommercialPolicy
                )
            }
        }
    }

    private suspend fun saveTokens(accessToken: String, refreshToken: String) {
        withContext(Dispatchers.IO) {
            encryptedPreferences.edit().apply {
                putString(ACCESS_TOKEN_KEY, accessToken)
                putString(REFRESH_TOKEN_KEY, refreshToken)
            }.apply()

            _authState.update { currentState ->
                currentState.copy(
                    isAuthenticated = true,
                    isLoading = false,
                    error = null,
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            }
        }
    }
}