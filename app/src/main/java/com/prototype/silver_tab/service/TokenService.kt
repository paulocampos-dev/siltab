package com.prototype.silver_tab.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.prototype.silver_tab.data.repository.AuthRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * TokenService is a background service that periodically checks token validity
 * and refreshes tokens as needed.
 * Updated to use Hilt for dependency injection.
 */
@AndroidEntryPoint
class TokenService : Service() {
    companion object {
        private const val TAG = "TokenService"

        // Check token every 5 minutes
        private val TOKEN_CHECK_INTERVAL = TimeUnit.MINUTES.toMillis(5)
    }

    @Inject
    lateinit var authRepository: AuthRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var tokenCheckJob: Job? = null

    // Status monitoring
    private val _tokenStatus = MutableStateFlow<TokenStatus>(TokenStatus.Valid)
    val tokenStatus: StateFlow<TokenStatus> = _tokenStatus

    sealed class TokenStatus {
        object Valid : TokenStatus()
        object Refreshing : TokenStatus()
        data class Error(val message: String) : TokenStatus()
    }

    // Binder for client communication
    inner class TokenBinder : Binder() {
        fun getService(): TokenService = this@TokenService
    }

    override fun onBind(intent: Intent): IBinder {
        return TokenBinder()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("TokenService created")
        startTokenMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("TokenService destroyed")
        stopTokenMonitoring()
        serviceScope.cancel()
    }

    /**
     * Start periodic token checking
     */
    fun startTokenMonitoring() {
        if (tokenCheckJob?.isActive == true) {
            Timber.d("Token monitoring already active, not starting again")
            return
        }

        tokenCheckJob = serviceScope.launch {
            Timber.d("Starting token monitoring")
            try {
                while (isActive) {
                    checkAndRefreshTokenIfNeeded()
                    delay(TOKEN_CHECK_INTERVAL)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error in token monitoring loop")
                _tokenStatus.emit(TokenStatus.Error("Error in token monitoring: ${e.message}"))
            }
        }

        Timber.d("Token monitoring started")
    }

    /**
     * Stop periodic token checking
     */
    fun stopTokenMonitoring() {
        tokenCheckJob?.cancel()
        tokenCheckJob = null
        Timber.d("Token monitoring stopped")
    }

    /**
     * Force a token refresh now
     */
    fun forceTokenRefresh() {
        serviceScope.launch {
            try {
                Timber.d("Forcing token refresh")
                refreshToken()
            } catch (e: Exception) {
                Timber.e(e, "Error forcing token refresh")
                _tokenStatus.emit(TokenStatus.Error("Error forcing refresh: ${e.message}"))
            }
        }
    }

    /**
     * Check if token needs refreshing and refresh if needed
     */
    private suspend fun checkAndRefreshTokenIfNeeded() {
        try {
            Timber.d("Checking token validity")
            val token = authRepository.getAccessToken()

            if (token.isNullOrEmpty()) {
                Timber.w("Token is null or empty, need refresh")
                refreshToken()
            } else {
                Timber.d("Token is valid")
                _tokenStatus.emit(TokenStatus.Valid)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error checking token validity")
            _tokenStatus.emit(TokenStatus.Error("Error checking token: ${e.message}"))
        }
    }

    /**
     * Refresh the token
     */
    private suspend fun refreshToken() {
        _tokenStatus.emit(TokenStatus.Refreshing)
        Timber.d("Refreshing token")

        try {
            val result = authRepository.refreshToken()

            if (result.isSuccess) {
                Timber.d("Token refresh successful")
                _tokenStatus.emit(TokenStatus.Valid)
            } else {
                val errorMsg = "Token refresh failed: ${result.exceptionOrNull()?.message}"
                Timber.e(errorMsg)
                _tokenStatus.emit(TokenStatus.Error(errorMsg))
            }
        } catch (e: Exception) {
            val errorMsg = "Error refreshing token: ${e.message}"
            Timber.e(e, errorMsg)
            _tokenStatus.emit(TokenStatus.Error(errorMsg))
        }
    }

    /**
     * Clear all tokens
     */
    fun clearTokens() {
        serviceScope.launch {
            try {
                Timber.d("Clearing all tokens")
                authRepository.logout()
                _tokenStatus.emit(TokenStatus.Error("Tokens cleared"))
            } catch (e: Exception) {
                Timber.e(e, "Error clearing tokens")
                _tokenStatus.emit(TokenStatus.Error("Error clearing tokens: ${e.message}"))
            }
        }
    }
}