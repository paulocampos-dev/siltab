package com.prototype.silver_tab.ui.screens
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prototype.silver_tab.R
import retrofit2.HttpException
import com.prototype.silver_tab.data.api.RetrofitClient
import com.prototype.silver_tab.data.models.Car
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.PDI
import com.prototype.silver_tab.ui.components.*
import com.prototype.silver_tab.ui.dialogs.*
import com.prototype.silver_tab.ui.camera.*
import com.prototype.silver_tab.utils.CameraUtils
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.viewmodels.CheckScreenState
import com.prototype.silver_tab.viewmodels.CheckScreenViewModel
import com.prototype.silver_tab.viewmodels.SharedCarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun CheckScreen(
    viewModel: CheckScreenViewModel = viewModel(),
    selectedInspectionInfo: InspectionInfo?,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    sharedCarViewModel: SharedCarViewModel = viewModel()
) {
    val strings = LocalStringResources.current  // Add this line
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val cameraUtils = remember { CameraUtils(context) }
    val pdiList by sharedCarViewModel.listHistoricCars.collectAsState()

    var modelo by remember { mutableStateOf("") }
    // 4 states for the 4 help buttons
    var showHelpModalChassi by remember { mutableStateOf(false) }
    var showHelpModalSoc by remember { mutableStateOf(false) }
    var showHelpModalBateria by remember { mutableStateOf(false) }
    var showHelpModalPneus by remember { mutableStateOf(false) }


    if(showHelpModalChassi){
       HelpModal(
           onDismiss = { showHelpModalChassi = false },
           img = R.drawable.chassi,
           type = "chassi",
           strings = strings
        )
    }


    if(showHelpModalSoc){
        HelpModal(
            onDismiss = { showHelpModalSoc = false },
            img = R.drawable.soc,
            type = "soc",
            strings = strings
        )
    }


    if(showHelpModalPneus){
        HelpModal(
            onDismiss = { showHelpModalPneus = false },
            img = R.drawable.pneus,
            type = "pneu",
            strings = strings
        )
    }

    if(showHelpModalBateria){
        HelpModal(
            onDismiss = { showHelpModalBateria = false },
            img = 0,
            type = "",
            strings = strings
        )
    }



    LaunchedEffect(selectedInspectionInfo) {
        selectedInspectionInfo?.let { car ->
            viewModel.initializeWithCar(car)
            modelo = car.name?: ""
        }
    }

    val cameraState = rememberCameraManager(
        context = context,
        cameraUtils = cameraUtils,
        onImageCaptured = viewModel::onImageCaptured
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        VehicleInfoCard(selectedInspectionInfo = selectedInspectionInfo)

        // Chassis section
        HelpButton(onClick = {showHelpModalChassi = true})
        OutlinedTextField(
            value = state.chassisNumber,
            onValueChange = viewModel::updateChassisNumber,
            label = { Text(text = strings.chassisNumber, color = Color.White) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.Gray,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            )
        )

        ImageUploadField(
            title = strings.chassisPhoto,
            imageUri = state.chassisImageUri,
            onCameraClick = { cameraState.launchCamera(ImageType.CHASSIS) },
            onGalleryClick = { cameraState.launchGallery(ImageType.CHASSIS) },
            strings = strings
        )

        // SOC section
        HelpButton(onClick = {showHelpModalSoc = true})
        OutlinedTextField(
            value = state.socPercentage,
            onValueChange = viewModel::updateSocPercentage,
            label = { Text(text = strings.socPercentage, color = Color.White) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.Gray,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            )
        )

        ImageUploadField(
            title = strings.voltagePhoto,
            imageUri = state.batteryImageUri,
            onCameraClick = { cameraState.launchCamera(ImageType.BATTERY) },
            onGalleryClick = { cameraState.launchGallery(ImageType.BATTERY) },
            strings = strings
        )

        // Hybrid car section
        if (selectedInspectionInfo?.type?.contains("Hybrid", ignoreCase = true) == true) {
            HybridCarSection(
                batteryVoltage = state.batteryVoltage,
                voltageImageUri = state.voltageImageUri,
                onBatteryVoltageChange = viewModel::updateBatteryVoltage,
                onCameraClick = { cameraState.launchCamera(ImageType.VOLTAGE) },
                onGalleryClick = { cameraState.launchGallery(ImageType.VOLTAGE) }
            )
        }

        // Tire pressure section
        HelpButton(onClick = {showHelpModalPneus = true})
        TirePressureSection(
            frontLeftPressure = state.frontLeftPressure,
            frontRightPressure = state.frontRightPressure,
            rearLeftPressure = state.rearLeftPressure,
            rearRightPressure = state.rearRightPressure,
            onFrontLeftChange = viewModel::updateFrontLeftPressure,
            onFrontRightChange = viewModel::updateFrontRightPressure,
            onRearLeftChange = viewModel::updateRearLeftPressure,
            onRearRightChange = viewModel::updateRearRightPressure
        )

        ImageUploadField(
            title = strings.tirePressurePhoto,
            imageUri = state.tirePressureImageUri,
            onCameraClick = { cameraState.launchCamera(ImageType.TIRE_PRESSURE) },
            onGalleryClick = { cameraState.launchGallery(ImageType.TIRE_PRESSURE) },
            strings = strings
        )

        // Electric car section
        if (selectedInspectionInfo?.type?.contains("Eletric", ignoreCase = true) == true) {
            ElectricCarSection(
                isCarStarted = state.isCarStarted,
                carStartedImageUri = state.carStartedImageUri,
                onCarStartedChange = viewModel::updateCarStarted,
                onCameraClick = { cameraState.launchCamera(ImageType.CAR_STARTED) },
                onGalleryClick = { cameraState.launchGallery(ImageType.CAR_STARTED) }
            )
        }

        // Additional info section
        AdditionalInfoSection(
            additionalInfo = state.additionalInfo,
            onAdditionalInfoChange = viewModel::updateAdditionalInfo
        )

        Button(
            onClick = viewModel::showFinishDialog,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Green,
            )
        ) {
            Text(strings.finishPdi, color = Color.White)
        }
    }

    // Dialogs
    CancelDialog(
        show = state.showCancelDialog,
        onDismiss = viewModel::hideCancelDialog,
        onConfirm = onNavigateBack,
        strings = strings
    )
    Log.d("PDI_LIST", pdiList.joinToString(separator = "\n") { "Chassi: ${it.chassi}" })

    FinishDialog(
        show = state.showFinishDialog,
        onDismiss = viewModel::hideFinishDialog,
        onConfirm = {
            viewModel.hideFinishDialog()
            // Lançamos uma coroutine para executar as chamadas de rede de forma sequencial
            viewModel.viewModelScope.launch {
                // Se não houver carro com o chassi informado, faz o post do carro e aguarda sua conclusão
                if (pdiList.none { it.chassi == state.chassisNumber }) {
                    val re = Regex("[^A-Za-z0-9 ]")
                    val id = 10
                    postCarRequest(state, context, modelo, id)
                    postPdiRequest(state, context, id)
                }else {
                    postPdiRequest(state, context)
                }
                onFinish()
            }
        },
        strings = strings
    )

    // Depois mudar para ele pegar as coisas pelo chassi do carro e não pelo car_id.
    //Aí pegar o car id pelo chassi
    // Depois tenho que achar uma forma de ele gerar o car id automaticamente para as duas tabelas caso o carro seja novo

}

private suspend fun postPdiRequest(state: CheckScreenState, context: Context, id: Int? = null) {
    val inspectionDate = LocalDateTime.now()  // Data/hora atual
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    val formattedDate = inspectionDate.format(formatter)
    val pdi = if(id!=null){
        PDI(
            car_id = id, //ver como fazer para passar o car id e chassis correto agora
            PDI_id = null, //ver como passar corretamente também
            user_id = null, //ver como passar corretamente também
            dealer_code = "BYDAMERBR0070W", //tenho que passar pelo código
            created_at = formattedDate,
            chassi_number = state.chassisNumber,
            soc_percentage = state.socPercentage.toDouble(),
            battery12v = 58,
            tire_pressure_dd = state.frontRightPressure.toDouble(),
            tire_pressure_de = state.frontLeftPressure.toDouble(),
            tire_pressure_td = state.rearRightPressure.toDouble(),
            tire_pressure_te = state.rearLeftPressure.toDouble(),
            five_minutes_hybrid = state.isCarStarted,
            extra_text = state.additionalInfo
        )
    }else{
        PDI(
            car_id = id, //ver como fazer para passar o car id e chassis correto agora
            PDI_id = null, //ver como passar corretamente também
            user_id = null, //ver como passar corretamente também
            dealer_code = "BYDAMERBR0070W", //tenho que passar pelo código, pelo state provavelemte
            created_at = formattedDate,
            chassi_number = state.chassisNumber,
            soc_percentage = state.socPercentage.toDouble(),
            battery12v = 58,
            tire_pressure_dd = state.frontRightPressure.toDouble(),
            tire_pressure_de = state.frontLeftPressure.toDouble(),
            tire_pressure_td = state.rearRightPressure.toDouble(),
            tire_pressure_te = state.rearLeftPressure.toDouble(),
            five_minutes_hybrid = state.isCarStarted,
            extra_text = state.additionalInfo)
    }

    Log.d("PDI_DEBUG", "PDI a ser enviado:\n${pdi}")

    try {
        // Realiza a chamada na thread de IO
        val response = withContext(Dispatchers.IO) {
            RetrofitClient.pdiApi.postPdi(pdi)
        }
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "PDI enviado com sucesso!", Toast.LENGTH_SHORT).show()
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e("postPdiRequest", "Erro na resposta: $errorBody")
        }
    } catch (e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        Log.e("postPdiRequest", "Erro HTTP: ${e.message}, Body: $errorBody")
    } catch (e: IOException) {
        Log.e("postPdiRequest", "Erro de rede: ${e.message}")
    } catch (e: Exception) {
        Log.e("postPdiRequest", "Erro inesperado: ${e.message}")
    }
}

private suspend fun postCarRequest(state: CheckScreenState, context: Context, modelo: String, id: Int) {
    val re = Regex("[^A-Za-z0-9 ]")
    val car = Car(
        car_id = id,
        model = modelo,
        dealer_code = "BYDAMERBR0070W",  //ver como pegar pelo estado
        chassi_number = state.chassisNumber,
        pdi_ids = listOf(80, 90)   // ver também como será passado e tal
    )
    Log.d("PDI_DEBUG", "Car a ser enviado:\n${car}")

    try {
        // Realiza a chamada na thread de IO
        val response = withContext(Dispatchers.IO) {
            RetrofitClient.carsApi.postCar(car)
        }
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Car enviado com sucesso!", Toast.LENGTH_SHORT).show()
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e("postCarRequest", "Erro na resposta: $errorBody")
        }
    } catch (e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        Log.e("postCarRequest", "Erro HTTP: ${e.message}, Body: $errorBody")
    } catch (e: IOException) {
        Log.e("postCarRequest", "Erro de rede: ${e.message}")
    } catch (e: Exception) {
        Log.e("postCarRequest", "Erro inesperado: ${e.message}")
    }
}


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun CheckScreenPreview() {
//    MaterialTheme {
//        CheckScreen(
//            selectedInspectionInfo = InspectionInfo("Nome do Carro", "Tipo do Carro"),
//            onNavigateBack = {},
//            onFinish = {}
//        )
//    }
//}