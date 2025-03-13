package com.prototype.silver_tab

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.prototype.silver_tab.data.api_connection.RetrofitClient
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.data.store.LanguagePreferences
import com.prototype.silver_tab.data.store.UserPreferences
import com.prototype.silver_tab.logging.CrashReporting
import com.prototype.silver_tab.utils.Language
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
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var languagePreferences: LanguagePreferences

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // Implement the workManagerConfiguration property from Configuration.Provider
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    companion object {
        lateinit var instance: Context
            private set

        // These will be migrated to DI gradually, for now keeping for backward compatibility
        lateinit var authRepository: AuthRepository
            private set

        lateinit var userPreferences: UserPreferences
            private set

        lateinit var languagePreferences: LanguagePreferences
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize RetrofitClient with the auth repository
        RetrofitClient.initialize(authRepository)

        // Set companion object properties for backward compatibility
        Companion.authRepository = authRepository
        Companion.userPreferences = userPreferences
        Companion.languagePreferences = languagePreferences

        // Observe auth state changes to manage token refresh worker using application scope
        applicationScope.launch {
            authRepository.authState.collect { state ->
                if (state.isAuthenticated) {
                    // Start token refresh worker when authenticated
                    Timber.d("User authenticated, scheduling token refresh worker")
                    com.prototype.silver_tab.workers.TokenRefreshWorker.schedule(this@SilverTabApplication)
                } else {
                    // Cancel token refresh worker when not authenticated
                    Timber.d("User not authenticated, canceling token refresh worker")
                    com.prototype.silver_tab.workers.TokenRefreshWorker.cancel(this@SilverTabApplication)
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