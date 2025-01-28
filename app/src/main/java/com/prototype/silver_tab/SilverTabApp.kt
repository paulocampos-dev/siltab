package com.prototype.silver_tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prototype.silver_tab.ui.components.Car
import com.prototype.silver_tab.ui.screens.*
import com.prototype.silver_tab.ui.theme.BackgroundColor

enum class SilverTabApp {
    Login,
    WelcomeScreen,
    PDIStart,
    ChooseCar,
    CheckScreen
}

@Composable
fun SilverTabApp(
    navController: NavHostController = rememberNavController()
) {
    var selectedCar by remember { mutableStateOf<Car?>(null) }

    Scaffold(
        topBar = { /* TopBar logic */ }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SilverTabApp.Login.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = SilverTabApp.Login.name) {
                LoginScreen(
                    onLoginButtonClicked = {
                        navController.navigate(SilverTabApp.WelcomeScreen.name)
                    },
                    modifier = Modifier.background(BackgroundColor)
                )
            }

            composable(route = SilverTabApp.WelcomeScreen.name) {
                WelcomeScreen(
                    onPDIButtonClicked = {
                        navController.navigate(SilverTabApp.PDIStart.name)
                    },
                    {},
                    {},
                    modifier = Modifier.background(BackgroundColor)
                )
            }

            composable(route = SilverTabApp.PDIStart.name) {
                PDIStartScreen(
                    onPDIStartButtonClicked = {
                        navController.navigate(SilverTabApp.ChooseCar.name)
                    },
                    modifier = Modifier.background(BackgroundColor)
                )
            }

            composable(route = SilverTabApp.ChooseCar.name) {
                ChooseCar(
                    onCarSelected = { car ->
                        selectedCar = car
                        navController.navigate(SilverTabApp.CheckScreen.name)
                    },
                    modifier = Modifier.background(BackgroundColor)
                )
            }

            composable(route = SilverTabApp.CheckScreen.name) {
                CheckScreen(
                    selectedCar = selectedCar,
                    onNavigateBack = { navController.navigateUp() },
                    onFinish = {
                        // Handle finish action
                        selectedCar = null
                        navController.navigate(SilverTabApp.WelcomeScreen.name) {
                            popUpTo(SilverTabApp.WelcomeScreen.name) { inclusive = true }
                        }
                    },
                    modifier = Modifier.background(BackgroundColor)
                )
            }
        }
    }
}