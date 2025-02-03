package com.prototype.silver_tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.prototype.silver_tab.ui.screens.*
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.viewmodels.SharedCarViewModel

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
    navController: NavHostController = rememberNavController()
) {
    val sharedCarViewModel: SharedCarViewModel = viewModel()

    var selectedInspectionInfo by remember { mutableStateOf<InspectionInfo?>(null) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Determine if we should show the app bar
    val showAppBar = currentRoute != SilverTabScreen.Login.name

    // Determine if we should show the extended app bar with location info
    val showExtendedAppBar = currentRoute == SilverTabScreen.CheckScreen.name
    var showProfileModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (showAppBar) {
                AppBar(
                    canNavigateBack = navController.previousBackStackEntry != null,
                    showLocationInfo = showExtendedAppBar,
                    navigateUp = { navController.navigateUp() },
                    onLogoutButtonClicked = {
                        // Clear any necessary state
                        selectedInspectionInfo = null
                        navController.navigate(SilverTabScreen.Login.name) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onCancelClicked = {
                        if (currentRoute == SilverTabScreen.CheckScreen.name) {
                            navController.navigateUp()
                        }
                    },
                    onProfileButtonClicked = {
                        showProfileModal = true
                    },
                )
                if(showProfileModal){
                    ProfileModal(
                        profile = mockProfile,
                        onDismiss = { showProfileModal = false })
                }
            }
        }
    )
    {
        innerPadding ->
        NavHost(
            navController = navController,
            //startDestination = SilverTabScreen.Login.name,
            startDestination = SilverTabScreen.Login.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = SilverTabScreen.Test.name){
                PdiDataMock()
            } //só para ver se consegui dar fetch nos dados, pode excluir depois
            composable(route = SilverTabScreen.Login.name) {
                LoginScreen(
                    onLoginButtonClicked = {
                        navController.navigate(SilverTabScreen.WelcomeScreen.name)
                    },
                    modifier = Modifier.background(BackgroundColor)
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
                    sharedCarViewModel = sharedCarViewModel
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
                    navController.navigate("${SilverTabScreen.CheckScreen.name}/${car.chassi}")
                },
                modifier = Modifier.background(BackgroundColor)
            )
            }

            composable(
                route = "${SilverTabScreen.CheckScreen.name}/{carChassi}?",
                arguments = listOf(
                    navArgument("carChassi") {
                        type = NavType.StringType
                        nullable = true // Permite valores nulos
                        defaultValue = null // Valor padrão
                    }
                )
            ) { backStackEntry ->
                val carChassi = backStackEntry.arguments?.getString("carChassi")
                val listHistoricCars by sharedCarViewModel.listHistoricCars.collectAsState()
                val car = carChassi?.let { chassi ->
                    getCarByChassi(chassi, listHistoricCars)
                } ?: selectedInspectionInfo

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
                        modifier = Modifier.background(BackgroundColor)
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