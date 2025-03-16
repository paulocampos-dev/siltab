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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.ui.components.ProfileModal
import com.prototype.silver_tab.ui.screens.AppBar
import com.prototype.silver_tab.ui.screens.InspectionScreen
import com.prototype.silver_tab.ui.screens.LoginScreen
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.language.LocalizationProvider
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
    authViewModel: AuthViewModel = hiltViewModel() // Use Hilt for ViewModel
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

                /*composable(route = SilverTabScreen.PDIStart.name) {
                    // Direct use of PDIStartScreen with Hilt-injected ViewModels - no wrapper needed
                    *//*PDIStartScreen(
                        onPDIStartButtonClicked = {
                            navController.navigate(SilverTabScreen.ChooseCar.name)
                        },
                        modifier = Modifier.background(BackgroundColor),
                        onDealerButtonClicked = {
                            navController.navigate(SilverTabScreen.DealerScreen.name)
                        },
                        onChangeHistoricPDI = { car ->
                            navController.navigate("${SilverTabScreen.CheckScreen.name}/${car.vin}?isCorrection=true")
                        },
                        onNewPdi = { car ->
                            // Normalize the type
                            val normalizedType = when (car.type?.lowercase()) {
                                "híbrido", "hybrid", "hibrido" -> "hybrid"
                                "elétrico", "electric", "eletrico" -> "electric"
                                else -> car.type // Keep original if not recognized
                            }

                            val carWithoutInfo = InspectionInfo(
                                vin = car.vin,
                                name = car.name,
                                type = normalizedType,
                            )
                            selectedInspectionInfo = carWithoutInfo
                            navController.navigate("${SilverTabScreen.CheckScreen.name}/${carWithoutInfo.vin}?isNew=true")
                        }
                    )*//*
                }

                composable(route = SilverTabScreen.ChooseCar.name) {
                    ChooseCar(
                        onCarSelected = { car ->
                            selectedInspectionInfo = car
                            // Navigate passing the chassis as a parameter
                            navController.navigate("${SilverTabScreen.CheckScreen.name}/${car.vin}?isNew=true")
                        },
                        modifier = Modifier.background(BackgroundColor),
                    )
                }

                composable(
                    route = "${SilverTabScreen.CheckScreen.name}/{carChassi}?isNew={isNew}&isCorrection={isCorrection}",
                    arguments = listOf(
                        navArgument("carChassi") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("isNew") {
                            type = NavType.BoolType
                            defaultValue = false
                        },
                        navArgument("isCorrection") {
                            type = NavType.BoolType
                            defaultValue = false
                        }
                    )
                ) { backStackEntry ->
                    val isNew = backStackEntry.arguments?.getBoolean("isNew") ?: false
                    val isCorrection = backStackEntry.arguments?.getBoolean("isCorrection") ?: false
                    val carChassi = backStackEntry.arguments?.getString("carChassi")
                    val listHistoricCars by sharedCarViewModel.listHistoricCars.collectAsState()

                    // Get car details based on the navigation parameters
                    val car = when {
                        isCorrection -> {
                            // For a correction, find the car with matching vin
                            carChassi?.let { chassi ->
                                listHistoricCars.find { it.vin == chassi }
                            }
                        }
                        !isNew -> {
                            carChassi?.let { chassi ->
                                getCarByChassi(chassi, listHistoricCars) ?: selectedInspectionInfo?.takeIf { it.vin == chassi }
                            }
                        }
                        else -> {
                            selectedInspectionInfo
                        }
                    }

                    if (car != null) {
                        // Use Hilt for ViewModel
                        val viewModel: CheckScreenViewModel = hiltViewModel()

                        // Initialize the ViewModel with car data
                        LaunchedEffect(car) {
                            viewModel.handleEvent(CheckScreenEvent.InitializeWithCar(car))
                        }

                        CheckScreen(
                            viewModel = viewModel,
                            selectedInspectionInfo = car,
                            isCorrection = isCorrection,
                            dealerViewModel = dealerViewModel,
                            onNavigateBack = { navController.navigateUp() },
                            sharedCarViewModel = sharedCarViewModel,
                            onFinish = {
                                selectedInspectionInfo = null
                                Timber.d("CheckScreen finished, navigating to PDIStart")
                                // Use popUpTo to ensure we're not just adding to the back stack
                                navController.navigate(SilverTabScreen.PDIStart.name) {
                                    popUpTo(SilverTabScreen.PDIStart.name) { inclusive = true }
                                }
                            },
                            modifier = Modifier.background(BackgroundColor)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(BackgroundColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Carro não encontrado",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }*/
            }
        }
    }
}