package com.prototype.silver_tab.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prototype.silver_tab.data.repository.TokenRepository
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 * TokenService is a background service that periodically checks token validity
 * and refreshes tokens as needed.
 */
class TokenService : Service() {
    companion object {
        private const val TAG = "TokenService"

        // Check token every 5 minutes
        private val TOKEN_CHECK_INTERVAL = TimeUnit.MINUTES.toMillis(5)
    }

    private val tokenRepository = TokenRepository.getInstance()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var tokenCheckJob: Job? = null

    // Status monitoring
    private val _tokenStatus = MutableLiveData<TokenStatus>()
    val tokenStatus: LiveData<TokenStatus> = _tokenStatus

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
        Log.d(TAG, "TokenService created")
        startTokenMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "TokenService destroyed")
        stopTokenMonitoring()
        serviceScope.cancel()
    }

    /**
     * Start periodic token checking
     */
    fun startTokenMonitoring() {
        if (tokenCheckJob?.isActive == true) return

        tokenCheckJob = serviceScope.launch {
            while (isActive) {
                checkAndRefreshTokenIfNeeded()
                delay(TOKEN_CHECK_INTERVAL)
            }
        }

        Log.d(TAG, "Token monitoring started")
    }

    /**
     * Stop periodic token checking
     */
    fun stopTokenMonitoring() {
        tokenCheckJob?.cancel()
        tokenCheckJob = null
        Log.d(TAG, "Token monitoring stopped")
    }

    /**
     * Force a token refresh now
     */
    fun forceTokenRefresh() {
        serviceScope.launch {
            refreshToken()
        }
    }

    /**
     * Check if token needs refreshing and refresh if needed
     */
    private suspend fun checkAndRefreshTokenIfNeeded() {
        try {
            when (val result = tokenRepository.getValidAccessToken()) {
                is TokenRepository.TokenResult.Valid -> {
                    _tokenStatus.postValue(TokenStatus.Valid)
                    Log.d(TAG, "Token is valid")
                }
                else -> {
                    Log.d(TAG, "Token needs refresh, attempting now")
                    refreshToken()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking token validity", e)
            _tokenStatus.postValue(TokenStatus.Error("Error checking token: ${e.message}"))
        }
    }

    /**
     * Refresh the token
     */
    private suspend fun refreshToken() {
        _tokenStatus.postValue(TokenStatus.Refreshing)
        Log.d(TAG, "Refreshing token")

        try {
            when (val result = tokenRepository.refreshTokens()) {
                is TokenRepository.TokenResult.Valid -> {
                    _tokenStatus.postValue(TokenStatus.Valid)
                    Log.d(TAG, "Token refresh successful")
                }
                is TokenRepository.TokenResult.Invalid -> {
                    val errorMsg = "Invalid token: ${result.reason}"
                    _tokenStatus.postValue(TokenStatus.Error(errorMsg))
                    Log.e(TAG, errorMsg)
                }
                is TokenRepository.TokenResult.NetworkError -> {
                    val errorMsg = "Network error during refresh: ${result.message}"
                    _tokenStatus.postValue(TokenStatus.Error(errorMsg))
                    Log.e(TAG, errorMsg)
                }
                is TokenRepository.TokenResult.Expired -> {
                    val errorMsg = "Token expired and couldn't be refreshed"
                    _tokenStatus.postValue(TokenStatus.Error(errorMsg))
                    Log.e(TAG, errorMsg)
                }
            }
        } catch (e: Exception) {
            val errorMsg = "Error refreshing token: ${e.message}"
            _tokenStatus.postValue(TokenStatus.Error(errorMsg))
            Log.e(TAG, errorMsg, e)
        }
    }

    /**
     * Helper method to check if token is valid
     */
    suspend fun isTokenValid(): Boolean {
        return tokenRepository.getValidAccessToken() is TokenRepository.TokenResult.Valid
    }

    /**
     * Clear all tokens
     */
    fun clearTokens() {
        serviceScope.launch {
            tokenRepository.clearAllTokens()
            _tokenStatus.postValue(TokenStatus.Error("Tokens cleared"))
        }
    }
}