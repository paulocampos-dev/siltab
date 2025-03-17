package com.prototype.silver_tab.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.prototype.silver_tab.data.routes.AuthRoutes
import com.prototype.silver_tab.data.models.auth.AuthState
import com.prototype.silver_tab.data.models.auth.LoginRequest
import com.prototype.silver_tab.data.models.auth.RefreshTokenRequest
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRoutes: AuthRoutes
) {
    val tag = "AuthRepository"
    private val _authState = MutableStateFlow(AuthState())
    val authState : StateFlow<AuthState> = _authState.asStateFlow()

    // Create encrypted preferences for token storage
    private val encryptedPreferences: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Storage keys
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


    // Initialize and load stored auth data
    fun initialize() {
        try {
            val accessToken = encryptedPreferences.getString(ACCESS_TOKEN_KEY, null)
            val refreshToken = encryptedPreferences.getString(REFRESH_TOKEN_KEY, null)

            if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                val userId = encryptedPreferences.getInt(USER_ID_KEY, 0)
                val username = encryptedPreferences.getString(USERNAME_KEY, null)
                val email = encryptedPreferences.getString(EMAIL_KEY, null)
                val role = encryptedPreferences.getInt(ROLE_KEY, 0)
                val roleName = encryptedPreferences.getString(ROLE_NAME_KEY, null)
                val position = encryptedPreferences.getInt(POSITION_KEY, 0)
                val positionName = encryptedPreferences.getString(POSITION_NAME_KEY, null)
                val userEntityAuthority = encryptedPreferences.getString(USER_ENTITY_AUTHORITY_KEY, null)
                val hasCommercialPolicyAccess = encryptedPreferences.getString(COMMERCIAL_POLICY_ACCESS_KEY, null)

                // Update Auth State with all profile data
                _authState.value = AuthState(
                    isAuthenticated = true,
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    userId = userId,
                    username = username,
                    email = email,
                    role = role,
                    roleName = roleName,
                    position = position,
                    positionName = positionName,
                    userEntityAuthority = userEntityAuthority,
                    hasCommercialPolicyAccess = hasCommercialPolicyAccess
                )
            }
        } catch (e: Exception) {
            logTimber(tag, "Error during auth state initialization: ${e.message}")
        }
    }


    // Login method
    // Update the login method in AuthRepository.kt to include all profile fields

    suspend fun login(username: String, password: String): Result<Unit> {
        logTimber(tag, "Login attempt initiated for user: $username")
        try {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            val response = authRoutes.login(LoginRequest(username, password))

            if (response.isSuccessful) {
                logTimber(tag, "Login successful")
                val loginResponse = response.body()
                if (loginResponse != null) {
                    // Save auth data
                    encryptedPreferences.edit().apply {
                        putString(ACCESS_TOKEN_KEY, loginResponse.accessToken)
                        putString(REFRESH_TOKEN_KEY, loginResponse.refreshToken)
                        putInt(USER_ID_KEY, loginResponse.id)
                        putString(USERNAME_KEY, loginResponse.username)
                        putString(EMAIL_KEY, loginResponse.email)
                        putInt(ROLE_KEY, loginResponse.role ?: 0)
                        putString(ROLE_NAME_KEY, loginResponse.roleName)
                        putInt(POSITION_KEY, loginResponse.position ?: 0)
                        putString(POSITION_NAME_KEY, loginResponse.positionName)
                        putString(USER_ENTITY_AUTHORITY_KEY, loginResponse.userEntityAuthority)
//                        putString(COMMERCIAL_POLICY_ACCESS_KEY, loginResponse.hasCommercialPolicyAccess)
                    }.apply()

                    // Update auth state with all profile information
                    logTimber(tag, "Updating Auth State")
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        isLoading = false,
                        userId = loginResponse.id,
                        username = loginResponse.username,
                        accessToken = loginResponse.accessToken,
                        refreshToken = loginResponse.refreshToken,
                        email = loginResponse.email,
                        role = loginResponse.role,
                        roleName = loginResponse.roleName,
                        position = loginResponse.position,
                        positionName = loginResponse.positionName,
                        userEntityAuthority = loginResponse.userEntityAuthority,
//                        hasCommercialPolicyAccess = loginResponse.hasCommercialPolicyAccess
                    )

                    return Result.success(Unit)
                } else {
                    logTimber(tag, "Login failed: Empty response")
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = "Empty response"
                    )
                    return Result.failure(Exception("Empty response body"))
                }
            } else {
                logTimberError(tag, "Login failed: ${response.message()}")
                val errorMessage = "Login failed: ${response.message()}"
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
                return Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            logTimberError(tag, "Error during login: ${e.message}")
            val errorMessage = "Error during login: ${e.message}"
            _authState.value = _authState.value.copy(
                isLoading = false,
                error = errorMessage
            )
            return Result.failure(e)
        }
    }

    // Refresh token method
    suspend fun refreshToken(): Result<Unit> {
        logTimber(tag, "Starting token refresh")
        try {

            _authState.value = _authState.value.copy(isLoading = true)

            val currentRefreshToken = _authState.value.refreshToken
            if (currentRefreshToken.isNullOrEmpty()) {
                logTimber(tag, "Refresh token not available")
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = false,
                    error = "No refresh token available"
                )
                return Result.failure(Exception("No refresh token available"))
            }

            // Call your refresh token API
            logTimber(tag, "Calling refresh token API")
            val response = authRoutes.refreshToken(
                RefreshTokenRequest(refreshToken = currentRefreshToken)
            )

            if (response.isSuccessful) {
                logTimber(tag, "Token refresh successful")
                val refreshResponse = response.body()
                if (refreshResponse != null) {
                    // Save the new tokens
                    encryptedPreferences.edit().apply {
                        putString(ACCESS_TOKEN_KEY, refreshResponse.accessToken)
                        putString(REFRESH_TOKEN_KEY, refreshResponse.refreshToken)
                    }.apply()


                    // Update the auth state
                    logTimber(tag, "Updating Auth State")
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        error = null,
                        accessToken = refreshResponse.accessToken,
                        refreshToken = refreshResponse.refreshToken
                    )

                    return Result.success(Unit)
                } else {
                    logTimber(tag, "Empty refresh response")
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        error = "Empty refresh response"
                    )
                    return Result.failure(Exception("Empty refresh response body"))
                }
            } else {
                // If refresh fails, user is no longer authenticated
                logTimberError(tag, "Token refresh failed: ${response.message()}")
                _authState.value = AuthState(error = "Token refresh failed: ${response.message()}")
                encryptedPreferences.edit().clear().apply()
                return Result.failure(Exception("Token refresh failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            // If refresh throws an exception, user is no longer authenticated
            logTimberError(tag, "Refresh error: ${e.message}")
            _authState.value = AuthState(error = "Refresh error: ${e.message}")
            encryptedPreferences.edit().clear().apply()
            return Result.failure(e)
        }
    }

    // Get access token
    fun getAccessToken(): String? {
        return if (_authState.value.isAuthenticated) {
            _authState.value.accessToken
        } else {
            null
        }
    }


    fun logout(): Result<Unit> {
        logTimber(tag, "Logout initiated from user with AuthState: ${authState.value}")
        return try {
            // Clear stored tokens
            encryptedPreferences.edit()
                .remove(ACCESS_TOKEN_KEY)
                .remove(REFRESH_TOKEN_KEY)
                .remove(USER_ID_KEY)
                .remove(USERNAME_KEY)
//                .remove(EMAIL_KEY)
//                .remove(ROLE_KEY)
//                .remove(ROLE_NAME_KEY)
//                .remove(POSITION_KEY)
//                .remove(POSITION_NAME_KEY)
//                .remove(USER_ENTITY_AUTHORITY_KEY)
//                .remove(COMMERCIAL_POLICY_ACCESS_KEY)
                .apply()

            // Reset authentication state
            _authState.value = AuthState()

            logTimber(tag, "User logged out successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            logTimberError(tag, "Error during logout")

            // Even if there's an error clearing preferences, reset the auth state
            _authState.value = AuthState()
            Result.failure(e)
        }
    }

}
