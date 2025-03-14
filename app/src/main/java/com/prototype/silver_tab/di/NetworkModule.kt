package com.prototype.silver_tab.di

import com.prototype.silver_tab.BuildConfig
import com.prototype.silver_tab.data.api_connection.routes.AuthRoutes
import com.prototype.silver_tab.data.api_connection.routes.CarsApi
import com.prototype.silver_tab.data.api_connection.routes.DealerApi
import com.prototype.silver_tab.data.api_connection.routes.ImageRoutes
import com.prototype.silver_tab.data.api_connection.routes.PdiApi
import com.prototype.silver_tab.data.authenticator.TokenAuthenticator
import com.prototype.silver_tab.data.interceptor.AuthInterceptor
import com.prototype.silver_tab.data.repository.AuthRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    // Provide Retrofit instance
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl: String,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // Provide OkHttpClient with auth setup
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor,
        tokenAuthenticator: Authenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    // Provides Moshi instance
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // Provide API interfaces
    @Provides
    @Singleton
    fun provideAuthRoutes(retrofit: Retrofit): AuthRoutes {
        return retrofit.create(AuthRoutes::class.java)
    }

    @Provides
    @Singleton
    fun provideDealerApi(retrofit: Retrofit): DealerApi {
        return retrofit.create(DealerApi::class.java)
    }

    // Create logging interceptor for debugging
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

    // Create auth interceptor that adds tokens to requests
    @Provides
    @Singleton
    fun provideAuthInterceptor(authInterceptor: AuthInterceptor): Interceptor {
        return authInterceptor
    }


    // Create token authenticator to handle 401 responses
    @Provides
    @Singleton
    fun provideTokenAuthenticator(tokenAuthenticator: TokenAuthenticator): Authenticator {
        return tokenAuthenticator
    }
}