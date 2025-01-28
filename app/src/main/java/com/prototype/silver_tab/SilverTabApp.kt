package com.prototype.silver_tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prototype.silver_tab.ui.screens.ChooseCar
import com.prototype.silver_tab.ui.screens.LoginScreen
import com.prototype.silver_tab.ui.screens.PDIStartScreen
import com.prototype.silver_tab.ui.screens.WelcomeScreen
import com.prototype.silver_tab.ui.theme.BackgroundColor

// Here we control the screens name
enum class SilverTabApp {
    Login,
    WelcomeScreen,
    PDIStart,
    ChooseCar
}

@Composable
fun SilverTabApp(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        topBar = {//colocar aqui a lógica da topbar nas telas que eles existirem
             },
        ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SilverTabApp.Login.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = SilverTabApp.Login.name) {
                LoginScreen( onLoginButtonClicked = {
                    navController.navigate(SilverTabApp.WelcomeScreen.name)
                }, modifier = Modifier.background(BackgroundColor))
            }

            composable(route = SilverTabApp.WelcomeScreen.name){
                    WelcomeScreen(onPDIButtonClicked = {
                    navController.navigate(SilverTabApp.PDIStart.name)},
                        {},
                        {},
                        modifier = Modifier.background(BackgroundColor)
                    )
            }
            composable(route = SilverTabApp.PDIStart.name){
                PDIStartScreen({navController.navigate(SilverTabApp.ChooseCar.name)},
                    modifier = Modifier.background(BackgroundColor))
            }
            composable(route = SilverTabApp.ChooseCar.name){
                ChooseCar(onCarSelected = { car ->
                    navController.navigate(car.route ?: SilverTabApp.WelcomeScreen.name)
                },
                    modifier = Modifier.background(BackgroundColor))

            }
            composable("dolphin_route") { DolphinScreen() }
            composable("shark_route") { SharkScreen() }
            composable("han_route") { HanScreen() }
            composable("tan_route") { TanScreen() }
        }
    }
}
