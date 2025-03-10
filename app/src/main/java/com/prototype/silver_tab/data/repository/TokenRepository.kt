package com.prototype.silver_tab.data.repository

import android.util.Log
import com.prototype.silver_tab.SilverTabApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * TokenRepository acts as an adapter to the new AuthRepository system.
 * This class provides backward compatibility while we transition to using
 * AuthRepository directly.
 */
class TokenRepository {
    companion object {
        private const val TAG = "TokenRepository"

        // Singleton instance
        @Volatile private var instance: TokenRepository? = null

        fun getInstance(): TokenRepository {
            return instance ?: synchronized(this) {
                instance ?: TokenRepository().also { instance = it }
            }
        }
    }

    // Mutex for synchronizing token operations
    private val mutex = Mutex()

    // Get the AuthRepository instance
    private val authRepository = SilverTabApplication.authRepository

    /**
     * Represents the result of a token operation
     */
    sealed class TokenResult {
        data class Valid(val token: String) : TokenResult()
        object Expired : TokenResult()
        data class Invalid(val reason: String) : TokenResult()
        data class NetworkError(val message: String) : TokenResult()
    }

    /**
     * Gets a valid access token from the AuthRepository.
     *
     * @return TokenResult indicating success or specific failure reason
     */
    suspend fun getValidAccessToken(): TokenResult {
        return mutex.withLock {
            try {
                // Get the current auth state
                val currentState = authRepository.authState.first()

                if (!currentState.isAuthenticated || currentState.accessToken.isNullOrEmpty()) {
                    Log.d(TAG, "No access token found or not authenticated")
                    return TokenResult.Invalid("No access token available")
                }

                // Check if we need to refresh the token
                if (currentState.isLoading) {
                    Log.d(TAG, "Auth refresh is already in progress")
                    // Wait for refresh to complete
                    val refreshedState = authRepository.authState.first { !it.isLoading }

                    if (!refreshedState.isAuthenticated) {
                        return TokenResult.Invalid("Authentication failed after refresh")
                    }

                    return TokenResult.Valid(refreshedState.accessToken!!)
                }

                // If authenticated and token available, return it
                Log.d(TAG, "Using existing valid token")
                return TokenResult.Valid(currentState.accessToken)
            } catch (e: Exception) {
                Log.e(TAG, "Error getting valid access token: ${e.message}")
                return TokenResult.NetworkError("Error: ${e.message}")
            }
        }
    }

    /**
     * Stores both access and refresh tokens by delegating to AuthRepository
     */
    suspend fun storeTokens(accessToken: String, refreshToken: String) {
        // This function is now a no-op as tokens are managed by AuthRepository
        Log.d(TAG, "storeTokens() is deprecated - tokens are now managed by AuthRepository")
        // We don't have a direct API in AuthRepository to set tokens manually
        // as this should only happen during login or refresh
    }

    /**
     * Refreshes the access and refresh tokens.
     *
     * @return TokenResult with the new access token or an error
     */
    suspend fun refreshTokens(): TokenResult {
        return mutex.withLock {
            try {
                val result = authRepository.refreshToken()

                if (result.isSuccess) {
                    val newState = authRepository.authState.first()
                    if (newState.isAuthenticated && !newState.accessToken.isNullOrEmpty()) {
                        Log.d(TAG, "Token refresh successful")
                        return TokenResult.Valid(newState.accessToken)
                    } else {
                        Log.e(TAG, "Token refresh failed: State is not authenticated")
                        return TokenResult.Invalid("Token refresh failed")
                    }
                } else {
                    val errorMsg = "Token refresh failed: ${result.exceptionOrNull()?.message}"
                    Log.e(TAG, errorMsg)
                    return TokenResult.Invalid(errorMsg)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Token refresh error: ${e.message}", e)
                return TokenResult.NetworkError("Network error during refresh: ${e.message}")
            }
        }
    }

    /**
     * Clears all tokens and authentication state
     */
    suspend fun clearAllTokens() {
        mutex.withLock {
            try {
                authRepository.logout()
                Log.d(TAG, "All tokens cleared via AuthRepository")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing tokens: ${e.message}")
            }
        }
    }

    /**
     * This method is now a stub that always returns false.
     * Token expiration is handled internally by AuthRepository.
     */
    private fun isTokenAboutToExpire(token: String): Boolean {
        Log.d(TAG, "isTokenAboutToExpire() is deprecated - handled by AuthRepository")
        return false
    }
}