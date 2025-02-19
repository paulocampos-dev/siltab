package com.prototype.silver_tab

import android.app.Application
import android.content.Context
import com.prototype.silver_tab.data.manager.TokenManager
import com.prototype.silver_tab.data.store.LanguagePreferences
import com.prototype.silver_tab.data.store.UserPreferences
import com.prototype.silver_tab.utils.Language
import com.prototype.silver_tab.utils.LocalizationManager

class SilverTabApplication : Application() {
    companion object {
        lateinit var instance: Context
            private set

        lateinit var tokenManager: TokenManager
            private set

        lateinit var userPreferences: UserPreferences
            private set

        lateinit var languagePreferences: LanguagePreferences
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        tokenManager = TokenManager(this)
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