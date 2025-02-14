package com.prototype.silver_tab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.prototype.silver_tab.data.manager.TokenManager
import com.prototype.silver_tab.ui.theme.SilvertabTheme

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenManager = TokenManager(this)

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        setContent {
            SilvertabTheme {
                SilverTabApp()
            }
        }
    }
}