package com.prototype.silver_tab.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.prototype.silver_tab.data.api_connection.RetrofitClient

object AuthRepositoryProvider {
    private var instance: AuthRepository? = null

    fun getInstance(context: Context): AuthRepository {
        return instance ?: synchronized(this) {
            instance ?: createAuthRepository(context).also { instance = it }
        }
    }

    private fun createAuthRepository(context: Context): AuthRepository {
        // Create or retrieve the master key for encryption
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // Create the encrypted shared preferences
        val encryptedPrefs = EncryptedSharedPreferences.create(
            context,
            "auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return AuthRepositoryImpl(
            context,
            RetrofitClient.authRoutes,
            encryptedPrefs
        )
    }
}