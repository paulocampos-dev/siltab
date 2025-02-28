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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.common.net.HttpHeaders.TE
import com.prototype.silver_tab.data.repository.ImageRepository
import com.prototype.silver_tab.ui.components.DealerState
import com.prototype.silver_tab.viewmodels.CarsDataViewModelFactory
import com.prototype.silver_tab.viewmodels.PdiDataViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext


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

    Log.d("DealerViewModel", "Dealer Selecionado: $selectedDealer")
    Log.d("DealerViewModel", "Estado dos Dealers: $dealerState")
    
    val dealers = (dealerState as? DealerState.Success)?.dealers ?: emptyList()
    Log.d("DealerViewModel", "Len de Dealers ${dealers.size}")

    LaunchedEffect(authViewModel.isAuthenticated.collectAsState().value){
        if(authViewModel.isAuthenticated.value){
            dealerViewModel.refreshDealers()
        }
    }

    LaunchedEffect(dealers){
        if (dealers.size == 1 && selectedDealer == null){
            val dealer = dealers.first()
            dealerViewModel.selectDealer(dealer)
            Log.d("DealerViewModel", "Selecionado automaticamente: ${dealers.first().dealerCode}")
        }
    }
    

    var showDealerDialog by remember { mutableStateOf(false) }
    val canChangeDealers by userPreferences.hasPosition(2).collectAsState(initial = false)

    //Pdi api view model
    val viewModelPDI: PdiDataViewModel = viewModel(
        factory = PdiDataViewModelFactory(dealerViewModel)
    )
    val statePDI = viewModelPDI.pdiState.observeAsState().value ?: PdiState.Loading
    Log.d("DealerCode",  "Dealer code : $selectedDealer.")


    //Cars api view model
    val viewModelCars: CarsDataViewModel = viewModel(
        factory = CarsDataViewModelFactory(dealerViewModel)
    )
    //investigar por que está gerando 4 requisições e n 2


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
            viewModelPDI.loadData(it.dealerCode)  // Chama loadData() com o dealerCode atualizado
            viewModelCars.loadData(it.dealerCode)  // Chama loadData() com o dealerCode atualizado
        }
    }

    val dataCars = when(stateCars){
        is CarsState.Success ->{
            CarsDataMapped(stateCars.data)
        }
        else -> {
            emptyList()
        }
    }
    Log.d("DealerCode", "Dados dos carros: $dataCars")
    val carsMap = dataCars.associateBy { it["Car ID"] }
    Log.d("DealerCode", "Dados dos carros: $carsMap")
    Log.d("DealerCode", "Dados antes da filtragem: $statePDI")

    val filteredDataPDI = when (statePDI) {
        is PdiState.Success -> {
            Log.d("DealerCode", "Dados recebidos da API: ${statePDI.data}")
            PdiDataFiltered(statePDI.data, listOf("PDI ID", "Car ID",
                "Created At", "SOC Percentage",
                "Tire Pressure TD", "Tire Pressure DD",
                "Tire Pressure DE", "Tire Pressure TE", "Extra Text"))
        }
        else -> {
            emptyList()
        }
    }


    Log.d("DealerCode",  "PDI Filtered Data : $filteredDataPDI.")

    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    val listHistoricInspectionInfos: List<InspectionInfo> = filteredDataPDI
        .groupBy { it["Car ID"] }
        .mapNotNull { (carId, mapItems) ->
            val latestInspection = mapItems.maxByOrNull { mapItem ->
                val dateString = mapItem["Created At"]
                try {
                    if (dateString != null) LocalDateTime.parse(dateString, dateTimeFormatter) else LocalDateTime.MIN
                } catch (e: Exception) {
                    LocalDateTime.MIN
                }
            }

            fun chooseImage(model: String) : Int {
                var img = R.drawable.pid_car
                when (model) {
                    "BYD YUAN PLUS" -> img = R.drawable.byd_yuan_plus
                    "BYD TAN" -> img =  R.drawable.byd_tan
                    "BYD YUAN PRO" -> img = R.drawable.byd_yuan_pro
                    "BYD SEAL" -> img = R.drawable.pid_car
                    "BYD HAN" -> img = R.drawable.byd_han
                    "BYD DOLPHIN PLUS" -> img = R.drawable.byd_dolphin_plus
                    "BYD DOLPHIN" -> img = R.drawable.byd_dolphin
                    "BYD DOLPHIN MINI" -> img = R.drawable.byd_dolphin_mini
                    "BYD SONG PRO DM-i" -> img = R.drawable.byd_song_pro
                    "SONG PLUS PREMIUM DM-i" -> img = R.drawable.byd_song_plus
                    "BYD SONG PLUS DM-i" -> img = R.drawable.byd_song_plus
                    "BYD KING DM-i" -> img = R.drawable.byd_king
                    "BYD SHARK" -> img = R.drawable.byd_shark
                    else -> {
                    }
            }
                return img
            }

            latestInspection?.let { mapItem ->
                val model = carsMap[carId]?.get("Model") ?: "Unknown Model"
                Log.d("ChooseImage", "Model recebido: $model")
                val chassi = carsMap[carId]?.get("Vin") ?: "Chassi Desconhecido"
                Log.d("PDI_START_SCREEN", "PDI_ID: ${mapItem["PDI ID"]}")
                InspectionInfo(
                    name = model,
                    pdiId = mapItem["PDI ID"]?.toInt(),
                    image = chooseImage(model),
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

    var selectedInspectionInfo: InspectionInfo? by remember { mutableStateOf(null) }
    var searchCar by remember { mutableStateOf("") }
    val filteredCarList = listHistoricInspectionInfos
        .filter { it.chassi?.contains(searchCar, ignoreCase = true) ?: false }
        .sortedByDescending { it.date }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .pullRefresh(refreshState)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            if (canChangeDealers) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPDIStartButtonClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .wrapContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    ConstraintLayout(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Center)
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
                                    end.linkTo(button.end, margin = 0.dp)
                                    bottom.linkTo(button.bottom, margin = 0.dp)
                                }
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    .background(
                        color = Color(0xFFD9D9D9),
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
            ) {
                SearchBar(
                    query = searchCar,
                    onQueryChange = { searchCar = it },
                    placeholder = strings.searchCars
                )

                InspectionInfoList(inspectionInfoList = filteredCarList) { car ->
                    selectedInspectionInfo = car
                }
            }
        }

        // Pull to refresh indicator
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

        // Dialogs
        if (showDealerDialog) {
            DealerSelectionDialog(
                showDialog = true,
                onDismiss = { showDealerDialog = false },
                onDealerSelected = { dealer ->
                    dealerViewModel.selectDealer(dealer)
                },
                dealerState = dealerState
            )
        }

        selectedInspectionInfo?.let { car ->
            InpectionInfoModalDialog(
                inspectionInfo = car,
                onNewPdi = { onNewPdi(car) },
                onDismiss = { selectedInspectionInfo = null },
                onChangeHistoricPDI = { onChangeHistoricPDI(car) }
            )
        }
    }
}