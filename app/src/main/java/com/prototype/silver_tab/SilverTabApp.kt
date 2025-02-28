package com.prototype.silver_tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prototype.silver_tab.data.api.AuthManager
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.getCarByChassi
import com.prototype.silver_tab.data.models.mockProfile
import com.prototype.silver_tab.ui.components.ProfileModal
import com.prototype.silver_tab.ui.screens.AppBar
import com.prototype.silver_tab.ui.screens.CheckScreen
import com.prototype.silver_tab.ui.screens.ChooseCar
import com.prototype.silver_tab.ui.screens.DealerScreen
import com.prototype.silver_tab.ui.screens.LoginScreen
import com.prototype.silver_tab.ui.screens.PDIStartScreen
import com.prototype.silver_tab.ui.screens.TestImageApiScreen
import com.prototype.silver_tab.ui.screens.WelcomeScreen
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.utils.LocalizationProvider
import com.prototype.silver_tab.viewmodels.AuthViewModel
import com.prototype.silver_tab.viewmodels.DealerViewModel
import com.prototype.silver_tab.viewmodels.SharedCarViewModel
import kotlinx.coroutines.launch

enum class SilverTabScreen {
    Login,
    WelcomeScreen,
    PDIStart,
    ChooseCar,
    CheckScreen,
    DealerScreen,
    Test
}

@Composable
fun SilverTabApp(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    LocalizationProvider {

        val sharedCarViewModel: SharedCarViewModel = viewModel()
        val dealerViewModel: DealerViewModel = viewModel()
        val scope = rememberCoroutineScope()
        val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

        var selectedInspectionInfo by remember { mutableStateOf<InspectionInfo?>(null) }
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route

        // Determine if we should show the app bar
        val showAppBar = currentRoute != SilverTabScreen.Login.name
        var showProfileModal by remember { mutableStateOf(false) }

        // Effect to handle initial navigation based on auth state
        LaunchedEffect(isAuthenticated) {
            if (isAuthenticated && currentRoute == SilverTabScreen.Login.name) {
                navController.navigate(SilverTabScreen.WelcomeScreen.name) {
                    popUpTo(SilverTabScreen.Login.name) { inclusive = true }
                }
            }
        }


        Scaffold(
            topBar = {
                if (showAppBar) {
                    AppBar(
                        canNavigateBack = navController.previousBackStackEntry != null,
                        showLocationInfo = currentRoute == "CheckScreen/{carChassi}?isNew={isNew}",
                        navigateUp = { navController.navigateUp() },
                        onLogoutButtonClicked = {
                            scope.launch {
                                // Clear AuthManager tokens
                                AuthManager.clearTokens()

                                // Clear TokenManager tokens
                                SilverTabApplication.tokenManager.clearTokens()

                                // Clear UserPreferences
                                SilverTabApplication.userPreferences.clearUserData()

                                // Update Auth State
                                authViewModel.setAuthenticated(false)

                                // Clear any necessary state
                                selectedInspectionInfo = null

                                // Navigate back to login screen
                                navController.navigate(SilverTabScreen.Login.name) {
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
                        dealerViewModel = dealerViewModel
                    )
                    if (showProfileModal) {
                        ProfileModal(onDismiss = { showProfileModal = false })
                    }
                }
            }
        ) {
            innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (isAuthenticated) SilverTabScreen.WelcomeScreen.name else SilverTabScreen.Login.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = SilverTabScreen.Test.name){
                    TestImageApiScreen()
                } //só para ver se consegui dar fetch nos dados, pode excluir depois
                composable(route = SilverTabScreen.Login.name) {
                    LoginScreen(
                        onLoginButtonClicked = {
                            navController.navigate(SilverTabScreen.WelcomeScreen.name)
//                            navController.navigate(SilverTabScreen.Test.name)
                        },
                        modifier = Modifier.background(BackgroundColor),
                    )
                }

                composable(route = SilverTabScreen.WelcomeScreen.name) {
                    WelcomeScreen(
                        onPDIButtonClicked = {
                            navController.navigate(SilverTabScreen.PDIStart.name)
                        },
                        {},
                        {},
                        modifier = Modifier.background(BackgroundColor)
                    )
                }

                composable(route = SilverTabScreen.PDIStart.name) {
                    PDIStartScreen(
                        onPDIStartButtonClicked = {
                            navController.navigate(SilverTabScreen.ChooseCar.name)
                        },
                        modifier = Modifier.background(BackgroundColor),
                        onDealerButtonClicked = {
                            navController.navigate(SilverTabScreen.DealerScreen.name)
                        },
                        onChangeHistoricPDI = { car ->
                            navController.navigate("${SilverTabScreen.CheckScreen.name}/${car.chassi}")
                        },
                        sharedCarViewModel = sharedCarViewModel,
                        dealerViewModel = dealerViewModel,
                        onNewPdi = { car ->
                            val carWithoutInfo = InspectionInfo(
                                chassi = car.chassi,
                                name = car.name,
                                image = car.image,
                                type = car.type,
                            )
                            selectedInspectionInfo = carWithoutInfo
                            navController.navigate("${SilverTabScreen.CheckScreen.name}/${carWithoutInfo.chassi}?isNew=true")

                        }
                    )
                }

                composable(route = SilverTabScreen.DealerScreen.name){
                    DealerScreen(
                        profile = mockProfile,
                        onChangeDealerClicked = {},
                    )
                }

                composable(route = SilverTabScreen.ChooseCar.name) {
                    ChooseCar(
                    onCarSelected = { car ->
                        selectedInspectionInfo = car
                        // Navegue passando o chassi como parâmetro
                        navController.navigate("${SilverTabScreen.CheckScreen.name}/${car.chassi}?isNew=true")
                    },

                    modifier = Modifier.background(BackgroundColor),

                )
                }

                composable(
                    route = "${SilverTabScreen.CheckScreen.name}/{carChassi}?isNew={isNew}",
                    arguments = listOf(
                        navArgument("carChassi") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("isNew") {
                            type = NavType.BoolType
                            defaultValue = false
                        }
                    )
                ) { backStackEntry ->
                    val isNew = backStackEntry.arguments?.getBoolean("isNew") ?: false
                    val carChassi = backStackEntry.arguments?.getString("carChassi")
                    val listHistoricCars by sharedCarViewModel.listHistoricCars.collectAsState()

                    val car = if (!isNew) {
                        carChassi?.let { chassi ->
                            getCarByChassi(chassi, listHistoricCars) ?: selectedInspectionInfo?.takeIf { it.chassi == chassi }
                        }
                    } else {
                        selectedInspectionInfo
                    }
                    if (car != null) {
                        CheckScreen(
                            selectedInspectionInfo = car,
                            onNavigateBack = { navController.navigateUp() },
                            onFinish = {
                                selectedInspectionInfo = null
                                navController.navigate(SilverTabScreen.WelcomeScreen.name) {
                                    popUpTo(SilverTabScreen.WelcomeScreen.name) { inclusive = true }
                                }
                            },
                            modifier = Modifier.background(BackgroundColor),
                            sharedCarViewModel = sharedCarViewModel,
                            dealerViewModel = dealerViewModel
                        )
                    } else {
                        Text("Carro não encontrado")
                    }
                }

//            composable(route = SilverTabScreen.CheckScreen.name) {
//                CheckScreen(
//                    selectedCar = selectedCar,
//                    onNavigateBack = { navController.navigateUp() },
//                    onFinish = {
//                        selectedCar = null
//                        navController.navigate(SilverTabScreen.WelcomeScreen.name) {
//                            popUpTo(SilverTabScreen.WelcomeScreen.name) { inclusive = true }
//                        }
//                    },
//                    modifier = Modifier.background(BackgroundColor)
//                )
//            }
            }
        }
    }
}