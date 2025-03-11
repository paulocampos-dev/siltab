package com.prototype.silver_tab.ui.screens


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel

import com.prototype.silver_tab.R
import com.prototype.silver_tab.SilverTabApplication.Companion.userPreferences
import com.prototype.silver_tab.data.mappers.CarsDataMapped
import com.prototype.silver_tab.data.mappers.PdiDataFiltered
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.ui.components.DealerSelectionDialog
import com.prototype.silver_tab.ui.components.InpectionInfoModalDialog
import com.prototype.silver_tab.ui.components.InspectionInfoList
import com.prototype.silver_tab.ui.components.SearchBar
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.LocalizedDrawables
import com.prototype.silver_tab.utils.LocalizedImage
import com.prototype.silver_tab.viewmodels.AuthViewModel
import com.prototype.silver_tab.viewmodels.CarsDataViewModel
import com.prototype.silver_tab.viewmodels.CarsState
import com.prototype.silver_tab.viewmodels.DealerViewModel
import com.prototype.silver_tab.viewmodels.PdiDataViewModel
import com.prototype.silver_tab.viewmodels.PdiState
import com.prototype.silver_tab.viewmodels.SharedCarViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.data.models.CarResponse
import com.prototype.silver_tab.ui.components.DealerState
import com.prototype.silver_tab.ui.components.InspectionInfoCard
import com.prototype.silver_tab.ui.components.InspectionInfoSection
import com.prototype.silver_tab.viewmodels.CarsDataViewModelFactory
import com.prototype.silver_tab.viewmodels.PdiDataViewModelFactory
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PDIStartScreen(
    modifier: Modifier = Modifier,
    onPDIStartButtonClicked: () -> Unit,
    onNewPdi: (InspectionInfo) -> Unit,
    onDealerButtonClicked: () -> Unit,
    dealerViewModel: DealerViewModel,
    onChangeHistoricPDI: (InspectionInfo) -> Unit,
    sharedCarViewModel: SharedCarViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val strings = LocalStringResources.current
    val isRefreshing by authViewModel.isRefreshing.collectAsState()
    val selectedDealer by dealerViewModel.selectedDealer.collectAsState()
    val dealerState by dealerViewModel.dealerState.collectAsState()
    val dealers = (dealerState as? DealerState.Success)?.dealers ?: emptyList()

    var showNoDealerDialog by remember { mutableStateOf(false) }
    var showDealerDialog by remember { mutableStateOf(false) }
    val canChangeDealers by remember {
        SilverTabApplication.authRepository.authState.map { state ->
            state?.position != null && state.position >= 2L
        }
    }.collectAsState(initial = false)

    // Trigger dealer refreshes when authenticated or dealers update
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState(initial = false)
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            dealerViewModel.refreshDealers()
        }
    }
    LaunchedEffect(dealers) {
        if (dealers.size == 1 && selectedDealer == null) {
            dealerViewModel.selectDealer(dealers.first())
        }
    }

    // PDI and Cars view models and data processing remain unchanged...
    val viewModelPDI: PdiDataViewModel = viewModel(factory = PdiDataViewModelFactory(dealerViewModel))
    val statePDI = viewModelPDI.pdiState.observeAsState().value ?: PdiState.Loading

    val viewModelCars: CarsDataViewModel = viewModel(factory = CarsDataViewModelFactory(dealerViewModel))
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            authViewModel.refreshToken()
            dealerViewModel.refreshDealers()
            selectedDealer?.dealerCode?.let { dealerCode ->
                viewModelPDI.loadData(dealerCode)
                viewModelCars.loadData(dealerCode)
            }
        }
    )
    val stateCars = viewModelCars.carsState.observeAsState().value ?: CarsState.Loading

    LaunchedEffect(selectedDealer) {
        selectedDealer?.let {
            viewModelPDI.loadData(it.dealerCode)
            viewModelCars.loadData(it.dealerCode)
        }
    }

    // Data mapping and filtering logic for inspection info
    val dataCars = when (stateCars) {
        is CarsState.Success -> CarsDataMapped(stateCars.data)
        else -> emptyList()
    }
    val carsMap = dataCars.associateBy { it["Car ID"] }
    val dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val listHistoricInspectionInfos: List<InspectionInfo> = (when (statePDI) {
        is PdiState.Success -> PdiDataFiltered(
            statePDI.data,
            listOf("PDI ID", "Car ID", "Created At", "SOC Percentage",
                "Tire Pressure TD", "Tire Pressure DD",
                "Tire Pressure DE", "Tire Pressure TE", "Extra Text")
        )
        else -> emptyList()
    }).groupBy { it["Car ID"] }
        .mapNotNull { (carId, mapItems) ->
            val latestInspection = mapItems.maxByOrNull { mapItem ->
                val dateString = mapItem["Created At"]
                try {
                    if (dateString != null) java.time.LocalDateTime.parse(dateString, dateTimeFormatter)
                    else java.time.LocalDateTime.MIN
                } catch (e: Exception) {
                    java.time.LocalDateTime.MIN
                }
            }
            latestInspection?.let { mapItem ->
                val model = carsMap[carId]?.get("Model") ?: "Unknown Model"
                val chassi = carsMap[carId]?.get("Vin") ?: "Chassi Desconhecido"
                InspectionInfo(
                    name = model,
                    pdiId = mapItem["PDI ID"]?.toInt(),
                    image = when (model) {
                        "BYD YUAN PLUS" -> R.drawable.byd_yuan_plus
                        "BYD TAN" -> R.drawable.byd_tan
                        "BYD YUAN PRO" -> R.drawable.byd_yuan_pro
                        "BYD SEAL" -> R.drawable.pid_car
                        "BYD HAN" -> R.drawable.byd_han
                        "BYD DOLPHIN PLUS" -> R.drawable.byd_dolphin_plus
                        "BYD DOLPHIN" -> R.drawable.byd_dolphin
                        "BYD DOLPHIN MINI" -> R.drawable.byd_dolphin_mini
                        "BYD SONG PRO DM-i" -> R.drawable.byd_song_pro
                        "SONG PLUS PREMIUM DM-i", "BYD SONG PLUS DM-i" -> R.drawable.byd_song_premium
                        "BYD KING DM-i" -> R.drawable.byd_king
                        "BYD SHARK" -> R.drawable.byd_shark
                        else -> R.drawable.pid_car
                    },
                    type = mapItem["TYPE"],
                    chassi = chassi,
                    date = mapItem["Created At"],
                    soc = mapItem["SOC Percentage"]?.toFloatOrNull(),
                    DE = mapItem["Tire Pressure DE"]?.toFloatOrNull(),
                    DD = mapItem["Tire Pressure DD"]?.toFloatOrNull(),
                    TD = mapItem["Tire Pressure TD"]?.toFloatOrNull(),
                    TE = mapItem["Tire Pressure TE"]?.toFloatOrNull(),
                )
            }
        }
    LaunchedEffect(listHistoricInspectionInfos) {
        sharedCarViewModel.updateListHistoricCars(listHistoricInspectionInfos)
    }

    // Get all cars for this dealer
    LaunchedEffect(stateCars) {
        if (stateCars is CarsState.Success) {
            // Extract all car data (not just ones with PDI history)
            val allDealerCars = CarsDataMapped(stateCars.data).mapNotNull { carMap ->
                try {
                    CarResponse(
                        car_id = carMap["Car ID"]?.toIntOrNull(),
                        car_model_id = null,
                        dealer_code = carMap["Dealer code"] ?: "",
                        vin = carMap["Vin"] ?: "",
                        pdi_ids = null,
                        is_sold = carMap["is_sold"]?.toBoolean() ?: false
                    )
                } catch (e: Exception) {
                    Log.e("PDIStartScreen", "Error mapping car data: ${e.message}")
                    null
                }
            }

            // Update the shared view model with all cars
            sharedCarViewModel.updateListAllDealerCars(allDealerCars)
        }
    }

    var selectedInspectionInfo: InspectionInfo? by remember { mutableStateOf(null) }
    var searchCar by remember { mutableStateOf("") }
    val filteredCarList = listHistoricInspectionInfos
        .filter { it.chassi?.contains(searchCar, ignoreCase = true) ?: false }
        .sortedByDescending { it.date }

    // Use a single LazyColumn to make the whole screen scrollable.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .pullRefresh(refreshState)
            .imePadding()  // This helps adjust for the keyboard
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Dealer selection card
            if (canChangeDealers) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BackgroundColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(1.dp, Color.White, shape = RoundedCornerShape(20.dp))
                                .padding(8.dp)
                                .background(BackgroundColor, shape = RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showDealerDialog = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.LocationOn,
                                contentDescription = "Localização",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.selectDealer,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                selectedDealer?.let {
                                    Text(it.dealerCode, color = Color.Gray)
                                }
                            }
                            Icon(
                                Icons.Outlined.Settings,
                                contentDescription = "Configuração",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
            // Spacer between items
            item { Spacer(modifier = Modifier.height(16.dp)) }
            // Start Inspection button
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (selectedDealer == null) {
                                showNoDealerDialog = true
                            } else {
                                onPDIStartButtonClicked()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        ConstraintLayout(
                            modifier = Modifier.wrapContentSize()
                        ) {
                            val (button, car) = createRefs()
                            LocalizedImage(
                                drawableMap = LocalizedDrawables.pdiButton,
                                contentDescription = strings.startInspection,
                                modifier = Modifier.constrainAs(button) {
                                    width = Dimension.wrapContent
                                    height = Dimension.wrapContent
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }
                            )
                            Image(
                                painter = painterResource(R.drawable.pid_car),
                                contentDescription = "Car icon for stock button",
                                modifier = Modifier
                                    .fillMaxWidth(0.4f)
                                    .aspectRatio(2f)
                                    .constrainAs(car) {
                                        end.linkTo(button.end)
                                        bottom.linkTo(button.bottom)
                                    }
                            )
                        }
                    }
                }
            }

            // Spacer before list section
            item { Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium))) }
            // Search bar item
            item {
                SearchBar(
                    query = searchCar,
                    onQueryChange = { searchCar = it },
                    placeholder = strings.searchCars,
                )
            }
            // Inspection info list items
            items(filteredCarList) { car ->
                InspectionInfoCard(
                    inspectionInfo = car,
                    onClick = { selectedInspectionInfo = car }
                )
            }
        } // End LazyColumn

        // Pull-to-refresh indicator (stays on top)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.TopCenter)
        ) {
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = Color.White,
                contentColor = Color(0xFF7B1FA2),
                scale = true
            )
        }

        // Dealer selection dialog
        if (showDealerDialog) {
            DealerSelectionDialog(
                showDialog = true,
                onDismiss = { showDealerDialog = false },
                onDealerSelected = { dealer -> dealerViewModel.selectDealer(dealer) },
                dealerState = dealerState
            )
        }
        // Inspection info modal dialog
        selectedInspectionInfo?.let { car ->
            InpectionInfoModalDialog(
                inspectionInfo = car,
                onNewPdi = { onNewPdi(car) },
                onDismiss = { selectedInspectionInfo = null },
                onChangeHistoricPDI = { onChangeHistoricPDI(car) }
            )
        }
    }

    // Show a dialog if no dealer is selected
    if (showNoDealerDialog) {
        AlertDialog(
            onDismissRequest = { showNoDealerDialog = false },
            title = { Text(text = strings.selectDealerRequired ?: "Selecione uma concessionária", color = Color.Black) },
            text = { Text(text = strings.selectDealerRequiredDesc ?: "Por favor, selecione uma concessionária antes de continuar.", color = Color.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        showNoDealerDialog = false
                        showDealerDialog = true
                    }
                ) {
                    Text(strings.selectDealer ?: "Selecionar Concessionária")
                }
            },
            dismissButton = {
                Button(onClick = { showNoDealerDialog = false }) {
                    Text(strings.close ?: "Fechar")
                }
            }
        )
    }
}

