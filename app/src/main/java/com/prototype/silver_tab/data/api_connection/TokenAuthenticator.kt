package com.prototype.silver_tab.data.api_connection

import android.util.Log
import com.prototype.silver_tab.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * This authenticator intercepts 401 responses and attempts to refresh the token.
 */
class TokenAuthenticator(
    private val authRepository: AuthRepository
) : Authenticator {

    private val lock = Object()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent infinite loops
        if (responseCount(response) >= 3) return null

        synchronized(lock) {
            return runBlocking {
                try {
                    val result = authRepository.refreshToken()

                    if (result.isSuccess) {
                        // Retry the request with the new token
                        val newToken = authRepository.getAccessToken()
                        if (!newToken.isNullOrEmpty()) {
                            return@runBlocking response.request.newBuilder()
                                .header("Authorization", "Bearer $newToken")
                                .build()
                        }
                    }

                    // If refresh failed, return null to indicate authentication failure
                    null
                } catch (e: Exception) {
                    Log.e("TokenAuthenticator", "Error refreshing token", e)
                    null
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            result++
            priorResponse = priorResponse.priorResponse
        }
        return result
    }
}