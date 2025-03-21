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
        }

        // For normal endpoints, add the token
        val authRepository = lazyAuthRepository.get()
        val token = runBlocking { authRepository.getAccessToken() }

        val newRequest = if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            // No token available
            request
        }

        // Move the chain.proceed() call outside of runBlocking to prevent potential deadlock
        return chain.proceed(newRequest)
    }
}