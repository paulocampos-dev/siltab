package com.prototype.silver_tab.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.prototype.silver_tab.data.api_connection.RetrofitClient
import com.prototype.silver_tab.data.api_connection.routes.AuthRoutes
import com.prototype.silver_tab.data.api_connection.routes.DealerApi
import com.prototype.silver_tab.data.api_connection.routes.ImageRoutes
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.data.repository.AuthRepositoryImpl
import com.prototype.silver_tab.data.repository.CheckScreenRepository
import com.prototype.silver_tab.data.repository.DealerRepository
import com.prototype.silver_tab.data.repository.ImageRepository
import com.prototype.silver_tab.data.repository.ImageRepositoryImpl
import com.prototype.silver_tab.data.repository.SharedCarRepository
import com.prototype.silver_tab.data.store.LanguagePreferences
import com.prototype.silver_tab.data.store.UserPreferences
import com.prototype.silver_tab.logging.CrashReporting
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        authRoutes: AuthRoutes,
        encryptedPreferences: SharedPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(context, authRoutes, encryptedPreferences)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideLanguagePreferences(@ApplicationContext context: Context): LanguagePreferences {
        return LanguagePreferences(context)
    }

    @Provides
    @Singleton
    fun provideImageRepository(imageRoutes: ImageRoutes): ImageRepository {
        return ImageRepositoryImpl(imageRoutes)
    }

    @Provides
    @Singleton
    fun provideImageRepositoryImpl(imageRoutes: ImageRoutes): ImageRepositoryImpl {
        return ImageRepositoryImpl(imageRoutes)
    }

    @Provides
    @Singleton
    fun provideCheckScreenRepository(imageRepositoryImpl: ImageRepositoryImpl): CheckScreenRepository {
        return CheckScreenRepository(imageRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideCrashReporting(@ApplicationContext context: Context): CrashReporting {
        return CrashReporting(context)
    }

    @Provides
    @Singleton
    fun provideTimberTree(crashReporting: CrashReporting): Timber.Tree {
        return crashReporting
    }

    @Provides
    @Singleton
    fun provideDealerRepository(dealerApi: DealerApi): DealerRepository {
        return DealerRepository(dealerApi)
    }


    @Provides
    @Singleton
    fun provideSharedCarRepository(): SharedCarRepository {
        return SharedCarRepository()
    }

}