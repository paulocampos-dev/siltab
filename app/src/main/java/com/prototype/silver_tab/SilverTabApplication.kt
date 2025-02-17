package com.prototype.silver_tab

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.prototype.silver_tab.data.manager.TokenManager
import com.prototype.silver_tab.data.store.UserPreferences


    class SilverTabApplication : Application() {
    companion object {
        lateinit var instance: Context
            private set
        lateinit var tokenManager: TokenManager
            private set
        lateinit var userPreferences: UserPreferences
            private set
    }

     override fun onCreate() {
         super.onCreate()
         instance = this
         tokenManager = TokenManager(this)
         userPreferences = UserPreferences(this)
    }
}
