package com.prototype.silver_tab

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.data.store.LanguagePreferences
import com.prototype.silver_tab.logging.CrashReporting
import com.prototype.silver_tab.session.AppSessionManager
import com.prototype.silver_tab.language.Language
import com.prototype.silver_tab.workers.TokenRefreshWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SilverTabApplication : Application(), Configuration.Provider {
    // Create application scope
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Injected dependencies
    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var languagePreferences: LanguagePreferences

    @Inject
    lateinit var appSessionManager: AppSessionManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // Implement the workManagerConfiguration property
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    companion object {
        lateinit var instance: Context
            private set

        // For backward compatibility until everything is migrated to DI
        lateinit var authRepository: AuthRepository
            private set


        lateinit var languagePreferences: LanguagePreferences
            private set

        lateinit var appSessionManager: AppSessionManager
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Set companion object properties for backward compatibility
        Companion.authRepository = authRepository
        Companion.languagePreferences = languagePreferences
        Companion.appSessionManager = appSessionManager

        // Initialize auth repository
        applicationScope.launch {
            try {
                authRepository.initialize()
                Timber.d("Auth repository initialized")
            } catch (e: Exception) {
                Timber.e(e, "Error initializing auth repository")
            }
        }

        // Observe auth state changes to manage token refresh worker
        applicationScope.launch {
            authRepository.authState.collect { state ->
                if (state.isAuthenticated) {
                    // Start token refresh worker when authenticated
                    Timber.d("User authenticated, scheduling token refresh worker")
                    TokenRefreshWorker.schedule(this@SilverTabApplication)
                } else {
                    // Cancel token refresh worker when not authenticated
                    Timber.d("User not authenticated, canceling token refresh worker")
                    TokenRefreshWorker.cancel(this@SilverTabApplication)

                    // Clear session data on logout
                    appSessionManager.clearSession()
                }
            }
        }

        // Initialize with system language or default to English
        applicationScope.launch {
            val systemLanguage = resources.configuration.locales[0].language
            val initialLanguage = when (systemLanguage) {
                "pt" -> Language.PORTUGUESE
                "zh" -> Language.CHINESE
                else -> Language.ENGLISH
            }

            // Only set language if not already set
            if (languagePreferences.language.first() == Language.ENGLISH && systemLanguage != "en") {
                languagePreferences.saveLanguage(initialLanguage.name)
            }
        }

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.plant(CrashReporting(this))
        } else {
            Timber.plant(CrashReporting(this))
        }

        Timber.d("SilverTabApplication initialized")
    }
}