package com.prototype.silver_tab.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    // The base URL for the main Java API
    const val BASE_URL = "http://192.168.224.128:8099/"

    // Logging interceptor for debugging network calls
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Authorization interceptor to add JWT token to requests (excluding auth endpoints)
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        if (request.url.encodedPath.startsWith("/auth")) {
            chain.proceed(request) // Skip auth header for login endpoints
        } else {
            val token = AuthManager.getAccessToken()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("No authentication token available")
            }
            val authenticatedRequest = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(authenticatedRequest)
        }
    }

    // OkHttpClient with interceptors
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    // Moshi instance for JSON parsing
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Function to create a Retrofit instance dynamically
    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // Expose API interfaces as lazy singletons
    val authApi: AuthApi by lazy { createRetrofit(BASE_URL).create(AuthApi::class.java) }
    val dealerApi: DealerApi by lazy { createRetrofit(BASE_URL).create(DealerApi::class.java) }
    val imageApi: ImageAPI by lazy { createRetrofit(BASE_URL).create(ImageAPI::class.java) }

    // Now all API calls go through the main Java backend
    // which will handle authentication and forwarding to the Python API
    val pdiApi: PdiApi by lazy { createRetrofit(BASE_URL).create(PdiApi::class.java) }
    val carsApi: CarsApi by lazy { createRetrofit(BASE_URL).create(CarsApi::class.java) }
}

// Singleton object to manage authentication tokens
object AuthManager {
    private var accessToken: String? = null
    private var refreshToken: String? = null

    fun setTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    fun getAccessToken(): String? = accessToken
    fun getRefreshToken(): String? = refreshToken
    fun clearTokens() {
        accessToken = null
        refreshToken = null
    }
}