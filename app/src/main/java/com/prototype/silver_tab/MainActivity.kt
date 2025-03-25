package com.prototype.silver_tab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.prototype.silver_tab.config.FieldConfigService
import com.prototype.silver_tab.ui.theme.SilvertabTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var fieldConfigService: FieldConfigService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Initializing MainActivity")

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        // Initialize field configuration
        lifecycleScope.launch {
            try {
                // For now, use the default configuration
                // In the future, you can call fetchFieldConfig() to get config from server
                fieldConfigService.resetToDefault()
                Timber.d("Field configuration initialized")
            } catch (e: Exception) {
                Timber.e(e, "Error initializing field configuration")
            }
        }

        setContent {
            SilvertabTheme {
                SilverTabApp()
            }
        }
    }
}
