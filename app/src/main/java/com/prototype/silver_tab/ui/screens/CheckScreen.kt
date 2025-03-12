package com.prototype.silver_tab.ui.screens.checkscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.repository.ImageRepository
import com.prototype.silver_tab.ui.components.checkscreen.AdditionalInfoSection
import com.prototype.silver_tab.ui.components.checkscreen.BatterySection
import com.prototype.silver_tab.ui.components.checkscreen.CancelDialog
import com.prototype.silver_tab.ui.components.checkscreen.ChassisSection
import com.prototype.silver_tab.ui.components.checkscreen.FinishDialog
import com.prototype.silver_tab.ui.components.checkscreen.HybridCarSection
import com.prototype.silver_tab.ui.components.checkscreen.ImageType
import com.prototype.silver_tab.ui.components.checkscreen.SocSection
import com.prototype.silver_tab.ui.components.checkscreen.TirePressureSection
import com.prototype.silver_tab.ui.components.checkscreen.VehicleInfoCard
import com.prototype.silver_tab.ui.components.checkscreen.rememberCameraManager
import com.prototype.silver_tab.ui.dialogs.SuccessDialog
import com.prototype.silver_tab.ui.theme.DarkGreen
import com.prototype.silver_tab.utils.CameraUtils
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.StringResources
import com.prototype.silver_tab.utils.convertImageDtoToUri
import com.prototype.silver_tab.utils.getModelIdFromName
import com.prototype.silver_tab.utils.validation.ValidationResult
import com.prototype.silver_tab.viewmodels.CheckScreenEvent
import com.prototype.silver_tab.viewmodels.CheckScreenViewModel
import com.prototype.silver_tab.viewmodels.DealerViewModel
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Refactored Check Screen using the new architecture
 */
@Composable
fun CheckScreen(
    viewModel: CheckScreenViewModel = viewModel(),
    selectedInspectionInfo: InspectionInfo?,
    isCorrection: Boolean = false,
    dealerViewModel: DealerViewModel,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStringResources.current
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val submissionState by viewModel.submissionState.collectAsState()
    val validationState by viewModel.validationState.collectAsState()

    // Camera management
    val cameraUtils = remember { CameraUtils(context) }
    val cameraState = rememberCameraManager(
        context = context,
        cameraUtils = cameraUtils,
        onImageCaptured = { type, uri ->
            viewModel.handleEvent(CheckScreenEvent.AddImage(type, uri))
        }
    )

    // Get user ID for submissions
    val userId by remember {
        SilverTabApplication.authRepository.authState.map { it?.userId ?: 0L }
    }.collectAsState(initial = 0L)


    val selectedDealer by dealerViewModel.selectedDealer.collectAsState()

    // Determine if 12V battery is required based on car model
    val requireBattery12V = selectedInspectionInfo?.name == "BYD DOLPHIN MINI" ||
            selectedInspectionInfo?.name == "BYD YUAN PLUS"

    val isFormValid = remember { mutableStateOf(false) }

    // Add near the top of the composable function
    LaunchedEffect(submissionState) {
        when (submissionState) {
            is CheckScreenViewModel.SubmissionState.Success -> {
                Timber.d("Submission successful, showing success dialog")
                viewModel.handleEvent(CheckScreenEvent.ShowSuccessDialog)
            }
            is CheckScreenViewModel.SubmissionState.Error -> {
                Timber.e("Submission error: ${(submissionState as CheckScreenViewModel.SubmissionState.Error).message}")
                // Show error message to user
            }
            else -> {} // Do nothing for other states
        }
    }

    // Update form validity when relevant fields change
    LaunchedEffect(
        state.chassisNumber,
        state.socPercentage,
        state.frontLeftPressure,
        state.frontRightPressure,
        state.rearLeftPressure,
        state.rearRightPressure,
        state.batteryVoltage,
        requireBattery12V
    ) {
        isFormValid.value = viewModel.validateForm(requireBattery12V)
    }

    // Initialize with car data
    LaunchedEffect(selectedInspectionInfo) {
        selectedInspectionInfo?.let { car ->
            viewModel.handleEvent(CheckScreenEvent.InitializeWithCar(car))
        }
    }

    // Load existing images for correction mode
    LaunchedEffect(selectedInspectionInfo, isCorrection) {
        if (isCorrection && selectedInspectionInfo?.pdiId != null) {
            try {
                val pdiId = selectedInspectionInfo.pdiId

                // Load all images
                val allPdiImages = ImageRepository.getAllPdiImages(pdiId) ?: emptyList()

                // Process images in batches to avoid memory issues
                val batchSize = 2
                allPdiImages.chunked(batchSize).forEach { batch ->
                    batch.forEach { imageDTO ->
                        try {
                            // Skip images with no ID
                            val imageId = imageDTO.imageId ?: return@forEach

                            // Convert to URI
                            val uri = convertImageDtoToUri(imageDTO)
                            uri?.let {
                                // Track the image ID
                                viewModel.trackImageId(uri, imageId)

                                // Add to the appropriate image list
                                val imageType = when {
                                    imageDTO.imageTypeName?.contains("vin", ignoreCase = true) == true ->
                                        ImageType.CHASSIS
                                    imageDTO.imageTypeName?.contains("soc", ignoreCase = true) == true ->
                                        ImageType.SOC
                                    imageDTO.imageTypeName?.contains("tire", ignoreCase = true) == true ->
                                        ImageType.TIRE_PRESSURE
                                    imageDTO.imageTypeName?.contains("battery", ignoreCase = true) == true ->
                                        ImageType.BATTERY_12VOLTAGE
                                    imageDTO.imageTypeName?.contains("extraImages", ignoreCase = true) == true ->
                                        ImageType.EXTRA_IMAGE
                                    else -> null
                                }

                                imageType?.let {
                                    viewModel.handleEvent(CheckScreenEvent.AddImage(it, uri))
                                }
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error processing image: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading PDI images: ${e.message}")
            }
        }
    }

    // Dialogs
    if (state.showCancelDialog) {
        CancelDialog(
            onDismiss = { viewModel.handleEvent(CheckScreenEvent.HideCancelDialog) },
            onConfirm = onNavigateBack,
            strings = strings
        )
    }

    if (state.showFinishDialog) {
        FinishDialog(
            onDismiss = { viewModel.handleEvent(CheckScreenEvent.HideFinishDialog) },
            // In your FinishDialog onConfirm callback
            onConfirm = {
                viewModel.handleEvent(CheckScreenEvent.HideFinishDialog)

                Timber.d("Starting PDI submission process")

                // Get model ID based on selected car
                val modelId = selectedInspectionInfo?.name?.let { getModelIdFromName(it) }

                // Submit the PDI
                selectedDealer?.let { dealer ->
                    Timber.d("Selected dealer: ${dealer.dealerCode}, Model ID: $modelId")
                    viewModel.handleEvent(
                        CheckScreenEvent.SubmitPdi(
                            context = context,
                            userId = userId,
                            dealerCode = dealer.dealerCode,
                            modelId = modelId,  // Include the model ID here
                            isCorrection = isCorrection
                        )
                    )
                } ?: run {
                    Timber.e("Cannot submit PDI: No dealer selected")
                    // Show an error to the user
                }
            },
            strings = strings
        )
    }

    if (state.showSuccessDialog) {
        SuccessDialog(
            show = true,
            onDismiss = {
                Timber.d("Success dialog dismissed, navigating back to PDI Start Screen")
                viewModel.handleEvent(CheckScreenEvent.HideSuccessDialog)
                // Make sure to execute onFinish
                onFinish()
            },
            chassiNumber = state.chassisNumber,
            strings = strings
        )
    }

    // Main content
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Car info
            VehicleInfoCard(selectedInspectionInfo = selectedInspectionInfo)

            // Chassis section
            ChassisSection(
                chassisNumber = state.chassisNumber,
                onChassisNumberChange = { value ->
                    viewModel.handleEvent(CheckScreenEvent.UpdateChassisNumber(value))
                },
                chassisError = validationState["chassisNumber"] is ValidationResult.Error,
                chassisImageUris = state.chassisImageUris,
                onCameraClick = { cameraState.launchCamera(ImageType.CHASSIS) },
                onGalleryClick = { cameraState.launchGallery(ImageType.CHASSIS) },
                onDeleteImage = { index ->
                    viewModel.handleEvent(CheckScreenEvent.RemoveImage(ImageType.CHASSIS, index))
                },
                showHelpModal = false, // Manage help modals in your UI
                onShowHelpModalChange = { /* Handle help modal state */ },
                strings = strings,
                enabled = selectedInspectionInfo?.chassi == null
            )

            // SOC section
            SocSection(
                socPercentage = state.socPercentage,
                onSocPercentageChange = { value ->
                    viewModel.handleEvent(CheckScreenEvent.UpdateSocPercentage(value))
                },
                socError = validationState["socPercentage"] is ValidationResult.Error,
                socImageUris = state.socImageUris,
                onCameraClick = { cameraState.launchCamera(ImageType.SOC) },
                onGalleryClick = { cameraState.launchGallery(ImageType.SOC) },
                onDeleteImage = { index ->
                    viewModel.handleEvent(CheckScreenEvent.RemoveImage(ImageType.SOC, index))
                },
                showHelpModal = false,
                onShowHelpModalChange = { /* Handle help modal state */ },
                strings = strings
            )

            // Tire pressure section
            TirePressureSection(
                frontLeftPressure = state.frontLeftPressure,
                onFrontLeftChange = { value ->
                    viewModel.handleEvent(CheckScreenEvent.UpdateFrontLeftPressure(value))
                },
                frontLeftError = validationState["frontLeftPressure"] is ValidationResult.Error,
                frontRightPressure = state.frontRightPressure,
                onFrontRightChange = { value ->
                    viewModel.handleEvent(CheckScreenEvent.UpdateFrontRightPressure(value))
                },
                frontRightError = validationState["frontRightPressure"] is ValidationResult.Error,
                rearLeftPressure = state.rearLeftPressure,
                onRearLeftChange = { value ->
                    viewModel.handleEvent(CheckScreenEvent.UpdateRearLeftPressure(value))
                },
                rearLeftError = validationState["rearLeftPressure"] is ValidationResult.Error,
                rearRightPressure = state.rearRightPressure,
                onRearRightChange = { value ->
                    viewModel.handleEvent(CheckScreenEvent.UpdateRearRightPressure(value))
                },
                rearRightError = validationState["rearRightPressure"] is ValidationResult.Error,
                tirePressureImageUris = state.tirePressureImageUris,
                onCameraClick = { cameraState.launchCamera(ImageType.TIRE_PRESSURE) },
                onGalleryClick = { cameraState.launchGallery(ImageType.TIRE_PRESSURE) },
                onDeleteImage = { index ->
                    viewModel.handleEvent(CheckScreenEvent.RemoveImage(ImageType.TIRE_PRESSURE, index))
                },
                showHelpModal = false,
                onShowHelpModalChange = { /* Handle help modal state */ },
                strings = strings
            )

            // Hybrid car section (conditionally)
            if (selectedInspectionInfo?.type?.contains("Híbrido", ignoreCase = true) == true) {
                HybridCarSection(
                    isCarStarted = state.isCarStarted,
                    onCarStartedChange = { value ->
                        viewModel.handleEvent(CheckScreenEvent.UpdateCarStarted(value))
                    }
                )
            }

            // Battery section (conditionally)
            if (requireBattery12V) {
                BatterySection(
                    batteryVoltage = state.batteryVoltage,
                    onBatteryVoltageChange = { value ->
                        viewModel.handleEvent(CheckScreenEvent.UpdateBatteryVoltage(value))
                    },
                    batteryVoltageError = validationState["batteryVoltage"] is ValidationResult.Error,
                    battery12VImageUris = state.battery12VImageUris,
                    onCameraClick = { cameraState.launchCamera(ImageType.BATTERY_12VOLTAGE) },
                    onGalleryClick = { cameraState.launchGallery(ImageType.BATTERY_12VOLTAGE) },
                    onDeleteImage = { index ->
                        viewModel.handleEvent(CheckScreenEvent.RemoveImage(ImageType.BATTERY_12VOLTAGE, index))
                    },
                    showHelpModal = false,
                    onShowHelpModalChange = { /* Handle help modal state */ },
                    strings = strings
                )
            }

            // Additional info section
            AdditionalInfoSection(
                additionalInfo = state.additionalInfo,
                onAdditionalInfoChange = { value ->
                    viewModel.handleEvent(CheckScreenEvent.UpdateAdditionalInfo(value))
                }
            )

            // Finish button
            Button(
                onClick = {
                    if (isFormValid.value) {
                        viewModel.handleEvent(CheckScreenEvent.ShowFinishDialog)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    // Change color based on form validity
                    containerColor = if (isFormValid.value) DarkGreen else Color.Gray,
                    // Ensure the text is always visible regardless of button color
                    contentColor = Color.White
                ),
                // Disable the button when form is invalid
                enabled = isFormValid.value
            ) {
                Text(strings.finishPdi, color = Color.White)
            }
        }

        // Loading overlay during submission
        if (submissionState is CheckScreenViewModel.SubmissionState.Submitting) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = strings.sendingData ?: "Sending data...",
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Dialog for confirming cancellation
 */
@Composable
fun CancelDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    strings: com.prototype.silver_tab.utils.StringResources
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.cancelConfirmation) },
        text = { Text(strings.cancelConfirmationMessage) },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onConfirm) {
                Text(strings.yes)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(strings.no)
            }
        }
    )
}

/**
 * Dialog for confirming PDI submission
 */
@Composable
fun FinishDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    strings: StringResources
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.finishPdi) },
        text = { Text(strings.finishConfirmationMessage) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(strings.finishConfirmation)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.no)
            }
        }
    )
}
