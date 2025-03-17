package com.prototype.silver_tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.PreferencesSerializer.defaultValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prototype.silver_tab.SilverTabApplication.Companion.appSessionManager
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.ui.components.ProfileModal
import com.prototype.silver_tab.ui.screens.AppBar
import com.prototype.silver_tab.ui.screens.InspectionScreen
import com.prototype.silver_tab.ui.screens.LoginScreen
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.language.LocalizationProvider
import com.prototype.silver_tab.session.AppSessionManager
import com.prototype.silver_tab.ui.screens.CheckScreen
import com.prototype.silver_tab.ui.screens.ChooseCarScreen
import com.prototype.silver_tab.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

enum class SilverTabScreen {
    LoginScreen,
    WelcomeScreen,
    InspectionScreen,
    ChooseCar,
    CheckScreen,
    DealerScreen,
    Test
}

@Composable
fun SilverTabApp(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    LocalizationProvider {
        // Use Hilt for ViewModels
        val scope = rememberCoroutineScope()
        val isAuthenticated by authViewModel.isAuthenticated.collectAsState(initial = false)

        var selectedInspectionInfo by remember { mutableStateOf<InspectionInfo?>(null) }
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route

        // Determine if we should show the app bar
        val showAppBar = currentRoute != SilverTabScreen.LoginScreen.name
        var showProfileModal by remember { mutableStateOf(false) }

          Scaffold(
            topBar = {
                if (showAppBar) {
                    AppBar(
                        canNavigateBack = navController.previousBackStackEntry != null,
                        showLocationInfo = currentRoute == "CheckScreen/{carChassi}?isNew={isNew}",
                        navigateUp = { navController.navigateUp() },
                        // Update the logout logic
                        onLogoutButtonClicked = {
                            scope.launch {
                                // Call logout on the AuthViewModel
                                authViewModel.logout()

                                // Reset UI state
                                selectedInspectionInfo = null

                                // Navigate back to login screen
                                navController.navigate(SilverTabScreen.LoginScreen.name) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onCancelClicked = {
                            if (currentRoute == "CheckScreen/{carChassi}?isNew={isNew}") {
                                navController.navigateUp()
                            }
                        },
                        onProfileButtonClicked = {
                            showProfileModal = true
                        },
//                        dealerViewModel = dealerViewModel
                    )
                    if (showProfileModal) {
                        ProfileModal(onDismiss = { showProfileModal = false })
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (isAuthenticated) SilverTabScreen.InspectionScreen.name else SilverTabScreen.LoginScreen.name,
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(route = SilverTabScreen.LoginScreen.name) {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(SilverTabScreen.InspectionScreen.name)
                        },
                        modifier = Modifier.background(BackgroundColor),
                    )
                }

                composable(route = SilverTabScreen.InspectionScreen.name ) {
                    InspectionScreen(
                        onStartNewInspection = {
                            navController.navigate(SilverTabScreen.ChooseCar.name)
                        },
                        onViewInspectionDetails = {
                            navController.navigate(SilverTabScreen.CheckScreen.name)
                        },
                        onUpdateInspection = {
                            navController.navigate(SilverTabScreen.CheckScreen.name)
                        }
                    )
                }

                composable(route = SilverTabScreen.ChooseCar.name) {
                    ChooseCarScreen(
                        onCarSelected = { carInfo ->
                            // Store in local variable
                            selectedInspectionInfo = carInfo

                            // Store in session manager
                            // dont know if I like this... TODO
                            scope.launch {
                                appSessionManager.selectInspection(carInfo)
                                // Navigate to CheckScreen with the selected car info
                                navController.navigate("${SilverTabScreen.CheckScreen.name}/${carInfo.vin ?: "new"}?isNew=true")
                            }
                        },
                        modifier = Modifier.background(BackgroundColor)
                    )
                }

                composable(
                    route = "${SilverTabScreen.CheckScreen.name}/{carChassi}?isNew={isNew}",
                    arguments = listOf(
                        navArgument("carChassi") { type = NavType.StringType },
                        navArgument("isNew") {
                            type = NavType.BoolType
                            defaultValue = true
                        }
                    )
                ) {
                    CheckScreen(
                        onSaveComplete = {
                            navController.popBackStack(SilverTabScreen.InspectionScreen.name, inclusive = false)
                        }
                    )
                }
            }
        }
    }
}