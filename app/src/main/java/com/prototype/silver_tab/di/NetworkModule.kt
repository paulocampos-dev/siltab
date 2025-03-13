package com.prototype.silver_tab.di

import com.prototype.silver_tab.BuildConfig
import com.prototype.silver_tab.data.api_connection.TokenAuthenticator
import com.prototype.silver_tab.data.api_connection.routes.AuthRoutes
import com.prototype.silver_tab.data.api_connection.routes.CarsApi
import com.prototype.silver_tab.data.api_connection.routes.DealerApi
import com.prototype.silver_tab.data.api_connection.routes.ImageRoutes
import com.prototype.silver_tab.data.api_connection.routes.PdiApi
import com.prototype.silver_tab.data.repository.AuthRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoAuthInterceptorOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeneralRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }
    }

    @Provides
    @Singleton
    @NoAuthInterceptorOkHttpClient
    fun provideNoAuthClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(authRepository: AuthRepository): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            if (request.url.encodedPath.contains("/auth")) {
                chain.proceed(request) // Skip adding auth header for login/refresh endpoints.
            } else {
                runBlocking {
                    val token = authRepository.getAccessToken()
                    if (token.isNullOrEmpty()) {
                        throw IllegalStateException("No authentication token available")
                    }
                    val authenticatedRequest = request.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(authenticatedRequest)
                }
            }
        }
    }

    @Provides
    @Singleton
    @AuthInterceptorOkHttpClient
    fun provideAuthClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor,
        authRepository: AuthRepository
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(TokenAuthenticator(authRepository))
            .build()
    }

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        @NoAuthInterceptorOkHttpClient okHttpClient: OkHttpClient,
        moshi: Moshi,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    @GeneralRetrofit
    fun provideGeneralRetrofit(
        @AuthInterceptorOkHttpClient okHttpClient: OkHttpClient,
        moshi: Moshi,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthRoutes(@AuthRetrofit retrofit: Retrofit): AuthRoutes {
        return retrofit.create(AuthRoutes::class.java)
    }

    @Provides
    @Singleton
    fun provideDealerApi(@GeneralRetrofit retrofit: Retrofit): DealerApi {
        return retrofit.create(DealerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideImageRoutes(@GeneralRetrofit retrofit: Retrofit): ImageRoutes {
        return retrofit.create(ImageRoutes::class.java)
    }

    @Provides
    @Singleton
    fun providePdiApi(@GeneralRetrofit retrofit: Retrofit): PdiApi {
        return retrofit.create(PdiApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCarsApi(@GeneralRetrofit retrofit: Retrofit): CarsApi {
        return retrofit.create(CarsApi::class.java)
    }
}