package com.prototype.silver_tab.ui.screens
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
import com.prototype.silver_tab.data.models.Car
import com.prototype.silver_tab.ui.components.*
import com.prototype.silver_tab.ui.dialogs.*
import com.prototype.silver_tab.ui.camera.*
import com.prototype.silver_tab.utils.CameraUtils
import com.prototype.silver_tab.viewmodels.CheckScreenViewModel

@Composable
fun CheckScreen(
    viewModel: CheckScreenViewModel = viewModel(),
    selectedCar: Car?,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val cameraUtils = remember { CameraUtils(context) }


    LaunchedEffect(selectedCar) {
        selectedCar?.let { car ->
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
        VehicleInfoCard(selectedCar = selectedCar)

        // Chassis section
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
        if (selectedCar?.type?.contains("Hybrid", ignoreCase = true) == true) {
            HybridCarSection(
                batteryVoltage = state.batteryVoltage,
                voltageImageUri = state.voltageImageUri,
                onBatteryVoltageChange = viewModel::updateBatteryVoltage,
                onCameraClick = { cameraState.launchCamera(ImageType.VOLTAGE) },
                onGalleryClick = { cameraState.launchGallery(ImageType.VOLTAGE) }
            )
        }

        // Tire pressure section
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
        if (selectedCar?.type?.contains("Eletric", ignoreCase = true) == true) {
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
            onFinish()
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckScreenPreview() {
    MaterialTheme {
        CheckScreen(
            selectedCar = Car("Nome do Carro", "Tipo do Carro"),
            onNavigateBack = {},
            onFinish = {}
        )
    }
}