package com.prototype.silver_tab.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        // Don't add token for auth endpoints
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

    private val sharedOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .build()

    private val sharedMoshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(sharedMoshi))
            .client(sharedOkHttpClient)
            .build()
    }


    val authApi: AuthApi by lazy { createRetrofit("http://192.168.224.240:8099").create(AuthApi::class.java) }
    val dealerApi: DealerApi by lazy { createRetrofit("http://192.168.224.240:8099").create(DealerApi::class.java) }
    val pdiApi: PdiApi by lazy { createRetrofit("http://192.168.224.241:8000").create(PdiApi::class.java) }
    val carsApi: CarsApi by lazy { createRetrofit("http://192.168.224.241:8000").create(CarsApi::class.java) }

}

// Singleton object to manage auth state
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



