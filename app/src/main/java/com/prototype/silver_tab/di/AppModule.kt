package com.prototype.silver_tab.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.prototype.silver_tab.data.routes.AuthRoutes
import com.prototype.silver_tab.data.routes.DealerApi
import com.prototype.silver_tab.data.routes.ImageRoutes
import com.prototype.silver_tab.data.authenticator.TokenAuthenticator
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.data.repository.CarRepository
import com.prototype.silver_tab.data.repository.DealerRepository
import com.prototype.silver_tab.data.routes.CarRoutes
import com.prototype.silver_tab.data.store.LanguagePreferences
import com.prototype.silver_tab.logging.CrashReporting
import com.prototype.silver_tab.session.AppSessionManager
import com.prototype.silver_tab.utils.ImageHandler
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
    fun provideAppSessionManager(): AppSessionManager {
        return AppSessionManager()
    }

    @Provides
    @Singleton
    fun provideLanguagePreferences(@ApplicationContext context: Context): LanguagePreferences {
        return LanguagePreferences(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        authRoutes: AuthRoutes,
    ): AuthRepository {
        return AuthRepository(context, authRoutes)
    }

    @Provides
    @Singleton
    fun provideCarRepository(
        carRoutes: CarRoutes,
    ): CarRepository {
        return CarRepository(carRoutes)
    }

    @Provides
    @Singleton
    fun provideDealerRepository(dealerApi: DealerApi): DealerRepository {
        return DealerRepository(dealerApi)
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
    fun provideImageHandler(@ApplicationContext context: Context): ImageHandler {
        return ImageHandler(context)
    }

}