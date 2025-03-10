package com.prototype.silver_tab

import android.app.Application
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.prototype.silver_tab.data.api_connection.RetrofitClient
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.data.repository.AuthRepositoryProvider
import com.prototype.silver_tab.data.store.LanguagePreferences
import com.prototype.silver_tab.data.store.UserPreferences
import com.prototype.silver_tab.utils.Language
import com.prototype.silver_tab.utils.LocalizationManager
import com.prototype.silver_tab.workers.TokenRefreshWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SilverTabApplication : Application() {
    // Create application scope
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        lateinit var instance: Context
            private set

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

        // Initialize auth repository first
        authRepository = AuthRepositoryProvider.getInstance(this)

        // Initialize RetrofitClient with the auth repository
        RetrofitClient.initialize(authRepository)

        // Observe auth state changes to manage token refresh worker using application scope
        applicationScope.launch {
            authRepository.authState.collect { state ->
                if (state.isAuthenticated) {
                    // Start token refresh worker when authenticated
                    TokenRefreshWorker.schedule(this@SilverTabApplication)
                } else {
                    // Cancel token refresh worker when not authenticated
                    TokenRefreshWorker.cancel(this@SilverTabApplication)
                }
            }
        }

        // Initialize preferences
        userPreferences = UserPreferences(this)
        languagePreferences = LanguagePreferences(this)

        // Initialize with system language or default to English
        val systemLanguage = resources.configuration.locales[0].language
        val initialLanguage = when (systemLanguage) {
            "pt" -> Language.PORTUGUESE
            "zh" -> Language.CHINESE
            else -> Language.ENGLISH
        }
    }
}