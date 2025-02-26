package com.prototype.silver_tab.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    const val BASE_URL = "http://192.168.15.19:8099"

    // Logging interceptor for debugging network calls
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Authorization interceptor to add JWT token to requests (skipping auth endpoints)
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        if (request.url.encodedPath.startsWith("/auth")) {
            chain.proceed(request)
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

    // Moshi instance with KotlinJsonAdapterFactory (add custom adapters if needed)
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Function to build a Retrofit instance using the shared OkHttp client and Moshi converter
    private fun retrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // Expose API interfaces as lazy singletons
    val authApi: AuthApi by lazy { retrofitInstance().create(AuthApi::class.java) }
    val dealerApi: DealerApi by lazy { retrofitInstance().create(DealerApi::class.java) }
    val pdiApi: PdiApi by lazy { retrofitInstance().create(PdiApi::class.java) }
    val carsApi: CarsApi by lazy { retrofitInstance().create(CarsApi::class.java) }
    val imageapi: ImageAPI by lazy { retrofitInstance().create(ImageAPI::class.java) }
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
