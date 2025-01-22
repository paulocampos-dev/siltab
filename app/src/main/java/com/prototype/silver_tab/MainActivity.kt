package com.prototype.silver_tab
import com.prototype.silver_tab.ui.theme.SilvertabTheme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SilvertabTheme {
                SilverTabApp()
            }
        }
    }
}