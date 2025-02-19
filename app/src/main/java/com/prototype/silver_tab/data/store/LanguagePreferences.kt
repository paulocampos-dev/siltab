package com.prototype.silver_tab.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.prototype.silver_tab.utils.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LanguagePreferences(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("language_prefs")
        private val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
    }

    val language: Flow<Language> = context.dataStore.data.map { preferences ->
        try {
            Language.valueOf(preferences[SELECTED_LANGUAGE] ?: Language.ENGLISH.name)
        } catch (e: IllegalStateException) {
            Language.ENGLISH
        }
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_LANGUAGE] = language
        }
    }
}