package com.prototype.silver_tab.ui.screens


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel

import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.mappers.CarsData
import com.prototype.silver_tab.data.mappers.CarsDataMapped
import com.prototype.silver_tab.data.mappers.PdiDataFiltered
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.ui.components.ConfirmationDialog
import com.prototype.silver_tab.ui.components.InspectionInfoList
import com.prototype.silver_tab.ui.components.InpectionInfoModalDialog
import com.prototype.silver_tab.ui.components.SearchBar
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.viewmodels.CarsDataViewModel
import com.prototype.silver_tab.viewmodels.CarsState
import com.prototype.silver_tab.viewmodels.PdiDataViewModel
import com.prototype.silver_tab.viewmodels.PdiState
import com.prototype.silver_tab.viewmodels.SharedCarViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun PDIStartScreen(
    modifier: Modifier = Modifier,
    onPDIStartButtonClicked: () -> Unit,
    onNewPdi: (InspectionInfo) -> Unit,
    onDealerButtonClicked: () -> Unit,
    onChangeHistoricPDI: (InspectionInfo) -> Unit,
    sharedCarViewModel: SharedCarViewModel = viewModel()
){

    //Pdi api view model
    val viewModel: PdiDataViewModel = viewModel()
    val state = viewModel.pdiState.observeAsState().value ?: PdiState.Loading

    //Cars api view model
    val viewModelCars: CarsDataViewModel = viewModel()
    val stateCars = viewModelCars.carsState.observeAsState().value ?: CarsState.Loading
    val dataCars = when(stateCars){
        is CarsState.Success ->{
            CarsDataMapped(stateCars.data)
        }
        else -> {
            emptyList()
        }
    }
    val carsMap = dataCars.associateBy { it["Car id"] }

    val filteredDataPDI = when (state) {
        is PdiState.Success -> {
            PdiDataFiltered(state.data, listOf("Car ID", "Chassi Number",
                "Inspection Date", "SOC Percentage",
                "Tire Pressure TD", "Tire Pressure DD",
                "Tire Pressure DE", "Tire Pressure TE"))
        }
        else -> {
            emptyList()
        }
    }

    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")



    val listHistoricInspectionInfos: List<InspectionInfo> = filteredDataPDI
        .groupBy { it["Car ID"] }
        .mapNotNull { (carId, mapItems) ->
            val latestInspection = mapItems.maxByOrNull { mapItem ->
                val dateString = mapItem["Inspection Date"]
                try {
                    if (dateString != null) LocalDateTime.parse(dateString, dateTimeFormatter) else LocalDateTime.MIN
                } catch (e: Exception) {
                    LocalDateTime.MIN
                }
            }

            latestInspection?.let { mapItem ->
                val model = carsMap[carId]?.get("Model") ?: "Unknown Model"
                InspectionInfo(
                    name = model,
                    chassi = mapItem["Chassi Number"],
                    date = mapItem["Inspection Date"],
                    soc = mapItem["SOC Percentage"]?.toFloatOrNull(),
                    DE = mapItem["Tire Pressure DE"]?.toFloatOrNull(),
                    DD = mapItem["Tire Pressure DD"]?.toFloatOrNull(),
                    TD = mapItem["Tire Pressure TD"]?.toFloatOrNull(),
                    TE = mapItem["Tire Pressure TE"]?.toFloatOrNull(),
                )
            }
        }

    LaunchedEffect(listHistoricInspectionInfos) {
        Log.d("PDI_LIST", "listHistoricInspectionInfos: $listHistoricInspectionInfos")
        sharedCarViewModel.updateListHistoricCars(listHistoricInspectionInfos)
    }



    //Provavelmente vou usar o car ID para pegar as infos do carro e mostrar
    // A partir do car id pegar a imagem e o modelo do carro
    // Car id é para o modelo ou é um id único? ACho que é um id único
    // Escolher o mais novo para mostrar no histórico
    //Precisa fazer uma api do modelo



    var selectedInspectionInfo: InspectionInfo? by remember { mutableStateOf(null) }
    var searchCar by remember { mutableStateOf("") }
    val filteredCarList = listHistoricInspectionInfos.filter {
        it.chassi?.contains(searchCar, ignoreCase = true) ?: false

    }.sortedByDescending { it.date }

    Column (modifier = Modifier
        .fillMaxSize()
        .background(BackgroundColor)
        ) {

        //card da concessionária

            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundColor)
            ) {

                Row(
                    modifier = Modifier.padding(16.dp)
                        .widthIn(max = 350.dp) //ajeitar isso para ficar alinhado
                        .border(1.dp, Color.White, shape = RoundedCornerShape(20.dp))
                        .padding(8.dp)
                        .background(BackgroundColor, shape = RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDealerButtonClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier.background(BackgroundColor)
                    ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = "Localização",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            "Nome da Concessionária",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Endereço da Concessionária", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = "Configuração",
                        tint = Color.White
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        //Botão para iniciar o pdi
        Button(
            onClick = onPDIStartButtonClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Unspecified
            ),
            shape = RectangleShape,
            modifier = Modifier.wrapContentWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                ConstraintLayout(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val (button, car) = createRefs()

                    Image(
                        painter = painterResource(R.drawable.pidstart_button),
                        contentDescription = "PDI Button",
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(button) {
                                width = Dimension.fillToConstraints
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
                                width = Dimension.fillToConstraints
                                height = Dimension.wrapContent
                                end.linkTo(button.end, margin = (0).dp)
                                bottom.linkTo(button.bottom, margin = 0.dp)
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height((dimensionResource(R.dimen.padding_small))))
        Column (modifier = Modifier.fillMaxWidth()
            .background(color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        ){

            //Criar barra de pesquisa
            SearchBar(query = searchCar,
                onQueryChange = {searchCar = it})




        // Lista de Pdis
        InspectionInfoList(inspectionInfoList = filteredCarList) { car ->
            selectedInspectionInfo = car
        }

        // Modal de detalhes dos pdis
            selectedInspectionInfo?.let { car ->
                InpectionInfoModalDialog(
                    inspectionInfo = car,
                    onNewPdi = { onNewPdi(car) },
                    onDismiss = { selectedInspectionInfo = null },
                    onChangeHistoricPDI = { onChangeHistoricPDI(car)},

                )
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewPDIStartScreen() {
    PDIStartScreen(
        onPDIStartButtonClicked = { },
        modifier = Modifier.fillMaxSize(),
        onDealerButtonClicked = {},
        onChangeHistoricPDI = {},
        onNewPdi = {}
    )
}








