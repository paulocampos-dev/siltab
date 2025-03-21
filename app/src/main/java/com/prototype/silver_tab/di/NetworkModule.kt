package com.prototype.silver_tab.di

import com.prototype.silver_tab.BuildConfig
import com.prototype.silver_tab.data.authenticator.TokenAuthenticator
import com.prototype.silver_tab.data.interceptor.AuthInterceptor
import com.prototype.silver_tab.data.routes.AuthRoutes
import com.prototype.silver_tab.data.routes.CarRoutes
import com.prototype.silver_tab.data.routes.DealerApi
import com.prototype.silver_tab.data.routes.ImageRoutes
import com.prototype.silver_tab.data.routes.PdiRoutes
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
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

    // Create retry interceptor that properly handles response resources
    private class RetryInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var retryCount = 0
            var lastException: IOException? = null

            while (retryCount < 3) {
                try {
                    // If this isn't our first attempt, add some backoff
                    if (retryCount > 0) {
                        logTimber("NetworkModule", "Retrying request (attempt $retryCount)")
                        Thread.sleep((1000 * retryCount).toLong())
                    }

                    // Return the response directly - no resource leaks
                    return chain.proceed(request)
                } catch (e: IOException) {
                    lastException = e
                    logTimberError("NetworkModule", "Request failed (attempt $retryCount): ${e.message}")
                    retryCount++

                    // If we've hit our max retries, throw the last exception
                    if (retryCount >= 3) {
                        break
                    }
                }
            }

            // If we got here, we failed all retries
            throw lastException ?: IOException("Request failed after 3 attempts")
        }
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
            .addInterceptor(RetryInterceptor())
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
    fun provideCarRoutes(retrofit: Retrofit): CarRoutes {
        return retrofit.create(CarRoutes::class.java)
    }

    @Provides
    @Singleton
    fun providePdiRoutes(retrofit: Retrofit): PdiRoutes {
        return retrofit.create(PdiRoutes::class.java)
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

    @Provides
    @Singleton
    fun provideImageRoutes(retrofit: Retrofit): ImageRoutes {
        return retrofit.create(ImageRoutes::class.java)
    }
}