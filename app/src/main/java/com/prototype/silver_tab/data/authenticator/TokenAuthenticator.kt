package com.prototype.silver_tab.data.authenticator

import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import com.prototype.silver_tab.data.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val lazyAuthRepository: Lazy<AuthRepository>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Don't attempt to refresh more than once
        if (response.request.header("Retry-Auth") != null) {
            return null
        }

        // Use a synchronized block to prevent multiple refresh attempts
        synchronized(this) {
            return runBlocking {
                val authRepository = lazyAuthRepository.get()
                val refreshResult = authRepository.refreshToken()

                if (refreshResult.isSuccess) {
                    val newToken = authRepository.getAccessToken()
                    if (newToken != null) {
                        // Retry the original request with the new token
                        response.request.newBuilder()
                            .header("Authorization", "Bearer $newToken")
                            .header("Retry-Auth", "true") // Mark as retried
                            .build()
                    } else {
                        null
                    }
                } else {
                    null // Refresh failed
                }
            }
        }
    }
}