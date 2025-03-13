package com.prototype.silver_tab.data.api_connection

import com.prototype.silver_tab.BuildConfig
import com.prototype.silver_tab.data.api_connection.routes.AuthRoutes
import com.prototype.silver_tab.data.api_connection.routes.CarsApi
import com.prototype.silver_tab.data.api_connection.routes.DealerApi
import com.prototype.silver_tab.data.api_connection.routes.ImageRoutes
import com.prototype.silver_tab.data.api_connection.routes.PdiApi
import com.prototype.silver_tab.data.repository.AuthRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

/**
 * This class is maintained for backward compatibility.
 * New code should use dependencies directly via Hilt.
 * This will be gradually phased out.
 */
object RetrofitClient {
    // The base URL for your Java backend.
    const val BASE_URL = BuildConfig.BASE_URL

    // Reference to the auth repository
    private lateinit var authRepository: AuthRepository

    // Logging interceptor for debugging network calls.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Initialize with auth repository
    fun initialize(authRepo: AuthRepository) {
        authRepository = authRepo
        Timber.d("RetrofitClient initialized with auth repository")
    }

    // Interceptor to add the access token to all requests (except auth endpoints).
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        if (request.url.encodedPath.contains("/auth")) {
            chain.proceed(request) // Skip adding auth header for login/refresh endpoints.
        } else {
            runBlocking {
                try {
                    val token = authRepository.getAccessToken()
                    if (token.isNullOrEmpty()) {
                        Timber.e("No authentication token available")
                        throw IllegalStateException("No authentication token available")
                    }
                    val authenticatedRequest = request.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(authenticatedRequest)
                } catch (e: Exception) {
                    Timber.e(e, "Error adding auth token to request")
                    throw e
                }
            }
        }
    }

    // Create a Moshi instance for JSON parsing.
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Create an OkHttpClient for authentication endpoints without the authenticator.
    private val authOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Retrofit instance for auth endpoints (login and refresh).
    private val authRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authOkHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // Expose authRoutes using the authRetrofit.
    val authRoutes: AuthRoutes by lazy {
        try {
            authRetrofit.create(AuthRoutes::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error creating AuthRoutes")
            throw e
        }
    }

    // Create an OkHttpClient for other endpoints, including our authenticator.
    private val okHttpClient: OkHttpClient by lazy {
        try {
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .authenticator(TokenAuthenticator(authRepository))
                .build()
        } catch (e: Exception) {
            Timber.e(e, "Error creating OkHttpClient")
            throw e
        }
    }

    // Function to create a Retrofit instance for non-auth endpoints.
    private fun createRetrofit(baseUrl: String): Retrofit {
        return try {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        } catch (e: Exception) {
            Timber.e(e, "Error creating Retrofit instance")
            throw e
        }
    }

    // Expose API interfaces as lazy singletons.
    val dealerApi: DealerApi by lazy {
        try {
            createRetrofit(BASE_URL).create(DealerApi::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error creating DealerApi")
            throw e
        }
    }

    val imageRoutes: ImageRoutes by lazy {
        try {
            createRetrofit(BASE_URL).create(ImageRoutes::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error creating ImageRoutes")
            throw e
        }
    }

    val pdiApi: PdiApi by lazy {
        try {
            createRetrofit(BASE_URL).create(PdiApi::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error creating PdiApi")
            throw e
        }
    }

    val carsApi: CarsApi by lazy {
        try {
            createRetrofit(BASE_URL).create(CarsApi::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error creating CarsApi")
            throw e
        }
    }
}