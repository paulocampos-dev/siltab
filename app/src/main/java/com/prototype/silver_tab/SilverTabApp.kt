package com.prototype.silver_tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.getCarByChassi
import com.prototype.silver_tab.data.models.mockProfile
import com.prototype.silver_tab.ui.components.ProfileModal
import com.prototype.silver_tab.ui.screens.AppBar
import com.prototype.silver_tab.ui.screens.ChooseCar
import com.prototype.silver_tab.ui.screens.DealerScreen
import com.prototype.silver_tab.ui.screens.LoginScreen
import com.prototype.silver_tab.ui.screens.PDIStartScreen
import com.prototype.silver_tab.ui.screens.WelcomeScreen
import com.prototype.silver_tab.ui.screens.checkscreen.CheckScreen
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.utils.LocalizationProvider
import com.prototype.silver_tab.viewmodels.AuthViewModel
import com.prototype.silver_tab.viewmodels.CheckScreenEvent
import com.prototype.silver_tab.viewmodels.CheckScreenViewModel
import com.prototype.silver_tab.viewmodels.DealerViewModel
import com.prototype.silver_tab.viewmodels.SharedCarViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

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
    authViewModel: AuthViewModel = hiltViewModel() // Use Hilt for ViewModel
) {
    LocalizationProvider {
        // Use Hilt for ViewModels
        val sharedCarViewModel: SharedCarViewModel = hiltViewModel()
        val dealerViewModel: DealerViewModel = hiltViewModel()
        val scope = rememberCoroutineScope()
        val isAuthenticated by authViewModel.isAuthenticated.collectAsState(initial = false)

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
                        // Update the logout logic
                        onLogoutButtonClicked = {
                            scope.launch {
                                // Call logout on the AuthViewModel
                                authViewModel.logout()

                                // Reset UI state
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
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (isAuthenticated) SilverTabScreen.PDIStart.name else SilverTabScreen.Login.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = SilverTabScreen.Test.name){
                    // Test screen - can be removed
                }

                composable(route = SilverTabScreen.Login.name) {
                    LoginScreen(
                        onLoginButtonClicked = {
                            navController.navigate(SilverTabScreen.PDIStart.name)
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
                    // Direct use of PDIStartScreen with Hilt-injected ViewModels - no wrapper needed
                    PDIStartScreen(
                        onPDIStartButtonClicked = {
                            navController.navigate(SilverTabScreen.ChooseCar.name)
                        },
                        modifier = Modifier.background(BackgroundColor),
                        onDealerButtonClicked = {
                            navController.navigate(SilverTabScreen.DealerScreen.name)
                        },
                        onChangeHistoricPDI = { car ->
                            navController.navigate("${SilverTabScreen.CheckScreen.name}/${car.chassi}?isCorrection=true")
                        },
                        onNewPdi = { car ->
                            // Normalize the type
                            val normalizedType = when (car.type?.lowercase()) {
                                "híbrido", "hybrid", "hibrido" -> "hybrid"
                                "elétrico", "electric", "eletrico" -> "electric"
                                else -> car.type // Keep original if not recognized
                            }

                            val carWithoutInfo = InspectionInfo(
                                chassi = car.chassi,
                                name = car.name,
                                image = car.image,
                                type = normalizedType,
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
                            // Navigate passing the chassis as a parameter
                            navController.navigate("${SilverTabScreen.CheckScreen.name}/${car.chassi}?isNew=true")
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
                            // For a correction, find the car with matching chassi
                            carChassi?.let { chassi ->
                                listHistoricCars.find { it.chassi == chassi }
                            }
                        }
                        !isNew -> {
                            carChassi?.let { chassi ->
                                getCarByChassi(chassi, listHistoricCars) ?: selectedInspectionInfo?.takeIf { it.chassi == chassi }
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
                }
            }
        }
    }
}