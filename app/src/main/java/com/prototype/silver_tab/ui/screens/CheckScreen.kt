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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prototype.silver_tab.R
import retrofit2.HttpException
import com.prototype.silver_tab.data.api.RetrofitClient
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.PDI
import com.prototype.silver_tab.ui.components.*
import com.prototype.silver_tab.ui.dialogs.*
import com.prototype.silver_tab.ui.camera.*
import com.prototype.silver_tab.utils.CameraUtils
import com.prototype.silver_tab.viewmodels.CheckScreenState
import com.prototype.silver_tab.viewmodels.CheckScreenViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CheckScreen(
    viewModel: CheckScreenViewModel = viewModel(),
    selectedInspectionInfo: InspectionInfo?,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val cameraUtils = remember { CameraUtils(context) }


    //4 states for the 4 help buttons
    var showHelpModalChassi by remember { mutableStateOf(false) }
    var showHelpModalSoc by remember { mutableStateOf(false) }
    var showHelpModalBateria by remember { mutableStateOf(false) }
    var showHelpModalPneus by remember { mutableStateOf(false) }


    if(showHelpModalChassi){
       HelpModal(
            onDismiss = { showHelpModalChassi = false },
            img = R.drawable.chassi,
           type = "chassi"
        )
    }


    if(showHelpModalSoc){
        HelpModal(
            onDismiss = { showHelpModalSoc = false },
            img = R.drawable.soc,
            type = "soc"
        )
    }


    if(showHelpModalPneus){
        HelpModal(
            onDismiss = { showHelpModalPneus = false },
            img = R.drawable.pneus,
            type = "pneu"
        )
    }

    if(showHelpModalBateria){
        HelpModal(
            onDismiss = { showHelpModalBateria = false },
            img = 0,
            type = ""
        )
    }



    LaunchedEffect(selectedInspectionInfo) {
        selectedInspectionInfo?.let { car ->
            viewModel.initializeWithCar(car)
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
            label = { Text("Chassi do veículo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        ImageUploadField(
            title = "Foto do Chassi",
            imageUri = state.chassisImageUri,
            onCameraClick = { cameraState.launchCamera(ImageType.CHASSIS) },
            onGalleryClick = { cameraState.launchGallery(ImageType.CHASSIS) }
        )

        // SOC section
        HelpButton(onClick = {showHelpModalSoc = true})
        OutlinedTextField(
            value = state.socPercentage,
            onValueChange = viewModel::updateSocPercentage,
            label = { Text("Percentual do SOC medido") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        ImageUploadField(
            title = "Foto da Bateria",
            imageUri = state.batteryImageUri,
            onCameraClick = { cameraState.launchCamera(ImageType.BATTERY) },
            onGalleryClick = { cameraState.launchGallery(ImageType.BATTERY) }
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
            title = "Foto da Pressão dos Pneus",
            imageUri = state.tirePressureImageUri,
            onCameraClick = { cameraState.launchCamera(ImageType.TIRE_PRESSURE) },
            onGalleryClick = { cameraState.launchGallery(ImageType.TIRE_PRESSURE) }
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
                .padding(vertical = 16.dp)
        ) {
            Text("Finalizar")
        }
    }



    // Dialogs
    CancelDialog(
        show = state.showCancelDialog,
        onDismiss = viewModel::hideCancelDialog,
        onConfirm = onNavigateBack
    )

    FinishDialog(
        show = state.showFinishDialog,
        onDismiss = viewModel::hideFinishDialog,
        onConfirm = {
            viewModel.hideFinishDialog()
            postPdiRequest(state, context)
            onFinish()
        }
    )


}

@OptIn(DelicateCoroutinesApi::class)
private fun postPdiRequest(state: CheckScreenState, context: Context){
    val inspectionDate = LocalDateTime.now()  // Pega a data e hora atuais
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")  // Formato TIMESTAMP(6)
    val formattedDate = inspectionDate.format(formatter)
    val pdi = PDI(
        car_id = "34290559D536451C96FBEA2855043DC9",
        inspector_id = 1,
        inspection_date = formattedDate,
        chassi_number = 3197,
        chassi_image_path = "/images/extra_1.jpg",
        soc_percentage = state.socPercentage.toDouble(),
        soc_percentage_image_path = "/images/extra_1.jpg",
        battery_12v = 58.0,
        battery_12v_image_path = "/images/extra_1.jpg",
        tire_pressure_dd = state.frontRightPressure.toDouble(),
        tire_pressure_de = state.frontLeftPressure.toDouble(),
        tire_pressure_td = state.rearRightPressure.toDouble(),
        tire_pressure_te = state.rearLeftPressure.toDouble(),
        tire_pressure_image_path = "/images/extra_1.jpg",
        five_minutes_hybrid = state.isCarStarted,
        extra_text = state.additionalInfo,
        extra_image_1 = "/images/extra_1.jpg",
        extra_image_2 = "/images/extra_2.jpg",
        extra_image_3 = "/images/extra_3.jpg")
    Log.d("PDI_DEBUG", "PDI a ser enviado:\n${pdi.toString()}")
    GlobalScope.launch(Dispatchers.IO) {

        try {
            val response = RetrofitClient.PdiApiService.postPdi(pdi)
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
}
//        if (response?.isSuccessful == true) {
//            withContext(Dispatchers.Main) {
//                AlertDialog.Builder(context)
//                    .setTitle("Sucesso")
//                    .setMessage("PDI enviado com sucesso!")
//                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//                    .show()
//            }
//        }



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