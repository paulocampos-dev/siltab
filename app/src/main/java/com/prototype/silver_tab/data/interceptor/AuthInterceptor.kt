package com.prototype.silver_tab.data.interceptor

import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import com.prototype.silver_tab.data.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val lazyAuthRepository: Lazy<AuthRepository>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Skip auth for login/refresh endpoints
        if (request.url.encodedPath.contains("/auth")) {
            return chain.proceed(request)
        } else {
            // For normal endpoints, add the token
            return runBlocking {
                val authRepository = lazyAuthRepository.get()
                val token = authRepository.getAccessToken()

                if (token != null) {
                    val authenticatedRequest = request.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(authenticatedRequest)
                } else {
                    // No token available
                    chain.proceed(request)
                }
            }
        }
    }
}