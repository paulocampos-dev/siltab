package com.prototype.silver_tab

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Here we control the screens name
enum class SilverTabApp {
    Login,
}

@Composable
fun SilverTabApp(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        topBar = {},
        ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SilverTabApp.Login.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = SilverTabApp.Login.name) {
                // Here we'll have the Login Screen
            }


        }
    }
}
