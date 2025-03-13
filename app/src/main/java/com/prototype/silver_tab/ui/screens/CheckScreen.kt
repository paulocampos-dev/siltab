package com.prototype.silver_tab.ui.screens.checkscreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prototype.silver_tab.R
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.ui.components.ImageUploadField
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
import com.prototype.silver_tab.ui.components.help.HelpModal
import com.prototype.silver_tab.ui.dialogs.DuplicateVinDialog
import com.prototype.silver_tab.ui.dialogs.SuccessDialog
import com.prototype.silver_tab.ui.theme.DarkGreen
import com.prototype.silver_tab.utils.CameraUtils
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.validation.ValidationResult
import com.prototype.silver_tab.viewmodels.CheckScreenEvent
import com.prototype.silver_tab.viewmodels.CheckScreenViewModel
import com.prototype.silver_tab.viewmodels.DealerViewModel
import com.prototype.silver_tab.viewmodels.SharedCarViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Refactored Check Screen using the new architecture with Hilt
 */
@Composable
fun CheckScreen(
    // Keep the original parameters for backward compatibility
    viewModel: CheckScreenViewModel = hiltViewModel(),
    selectedInspectionInfo: InspectionInfo?,
    isCorrection: Boolean = false,
    dealerViewModel: DealerViewModel = hiltViewModel(),
    sharedCarViewModel: SharedCarViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStringResources.current
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val submissionState by viewModel.submissionState.collectAsState()
    val validationState by viewModel.validationState.collectAsState()

    // Help modal states
    var showHelpModalChassi by remember { mutableStateOf(false) }
    var showHelpModalSoc by remember { mutableStateOf(false) }
    var showHelpModal12VBateria by remember { mutableStateOf(false) }
    var showHelpModalPneus by remember { mutableStateOf(false) }
    var showHelpModalHybrid by remember { mutableStateOf(false) }
    var showHelpModalInfo by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Determine if we should skip chassis validation
    // We skip validation if the chassis is pre-filled from the car selection
    val skipChassisValidation = selectedInspectionInfo?.chassi != null

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

    // Load existing images for correction mode
    LaunchedEffect(selectedInspectionInfo, isCorrection) {
        if (isCorrection && selectedInspectionInfo?.pdiId != null) {
            try {
                Timber.d("Loading existing PDI images for correction")
                viewModel.handleEvent(CheckScreenEvent.LoadExistingImages(
                    pdiId = selectedInspectionInfo.pdiId!!,
                    context = context
                ))
            } catch (e: Exception) {
                Timber.e(e, "Error loading existing PDI images")
            }
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
        isFormValid.value = viewModel.validateForm(requireBattery12V, skipChassisValidation)
    }

    // Monitor submission state and show success dialog when complete
    LaunchedEffect(submissionState) {
        when (submissionState) {
            is CheckScreenViewModel.SubmissionState.Success -> {
                Timber.d("Submission successful, showing success dialog")
                viewModel.handleEvent(CheckScreenEvent.ShowSuccessDialog)
            }
            is CheckScreenViewModel.SubmissionState.Error -> {
                val errorMessage = (submissionState as CheckScreenViewModel.SubmissionState.Error).message
                Timber.e("Submission error: $errorMessage")
                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
            }
            else -> {} // Do nothing for other states
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
            onConfirm = onConfirm@{
                viewModel.handleEvent(CheckScreenEvent.HideFinishDialog)

                // Add explicit null check for selectedDealer
                if (selectedDealer == null) {
                    Timber.e("Cannot submit PDI: No dealer selected")
                    Toast.makeText(context, "Cannot submit PDI: No dealer selected", Toast.LENGTH_LONG).show()
                    return@onConfirm
                }

                // If we get here, selectedDealer is not null
                Timber.d("Starting PDI submission process with dealer: ${selectedDealer!!.dealerCode}")

                // Get model ID based on selected car
                val modelId = selectedInspectionInfo?.name?.let { getModelIdFromName(it) }

                // Submit the PDI
                viewModel.handleEvent(
                    CheckScreenEvent.SubmitPdi(
                        context = context,
                        userId = userId,
                        dealerCode = selectedDealer!!.dealerCode,
                        modelId = modelId,
                        isCorrection = isCorrection
                    )
                )
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

    if (state.showDuplicateVinDialog) {
        DuplicateVinDialog(
            show = true,
            onDismiss = { viewModel.handleEvent(CheckScreenEvent.HideDuplicateVinDialog) },
            onFindInHistory = {
                viewModel.handleEvent(CheckScreenEvent.HideDuplicateVinDialog)
                onFinish()
            },
            strings = strings
        )
    }

    // Help modals
    if (showHelpModalChassi) {
        HelpModal(
            onDismiss = { showHelpModalChassi = false },
            img = R.drawable.chassi,
            type = "chassi",
            strings = strings
        )
    }

    if (showHelpModalSoc) {
        HelpModal(
            onDismiss = { showHelpModalSoc = false },
            img = R.drawable.soc,
            type = "SOC",
            strings = strings
        )
    }

    if (showHelpModalPneus) {
        HelpModal(
            onDismiss = { showHelpModalPneus = false },
            img = R.drawable.pneus,
            type = strings.tirePressure,
            strings = strings
        )
    }

    if (showHelpModal12VBateria) {
        HelpModal(
            onDismiss = { showHelpModal12VBateria = false },
            img = R.drawable.batteryhelpimage,
            type = strings.batteryVoltage,
            strings = strings
        )
    }

    if (showHelpModalHybrid) {
        HelpModal(
            onDismiss = { showHelpModalHybrid = false },
            img = null,
            type = "híbrido",
            strings = strings
        )
    }

    // Main content - form and fields
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
                showHelpModal = true,
                onShowHelpModalChange = { showHelpModalChassi = it },
                strings = strings,
                enabled = selectedInspectionInfo?.chassi == null,
                isLoading = state.isLoadingImages
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
                showHelpModal = true,
                onShowHelpModalChange = { showHelpModalSoc = it },
                strings = strings,
                isLoading = state.isLoadingImages
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
                showHelpModal = true,
                onShowHelpModalChange = { showHelpModalPneus },
                strings = strings,
                isLoading = state.isLoadingImages
            )

            // Hybrid car section (conditionally)
            if (selectedInspectionInfo?.type?.contains("Híbrido", ignoreCase = true) == true ||
                selectedInspectionInfo?.type?.contains("hybrid", ignoreCase = true) == true) {
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
                    showHelpModal = true,
                    onShowHelpModalChange = { showHelpModal12VBateria = it },
                    strings = strings,
                    isLoading = state.isLoadingImages
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Extra images section
            ImageUploadField(
                title = strings.extraImages,
                imageUris = state.extraImageUris,
                onCameraClick = { cameraState.launchCamera(ImageType.EXTRA_IMAGE) },
                onGalleryClick = { cameraState.launchGallery(ImageType.EXTRA_IMAGE) },
                onDeleteImage = { index ->
                    viewModel.handleEvent(CheckScreenEvent.RemoveImage(ImageType.EXTRA_IMAGE, index))
                },
                strings = strings,
                maxImages = 4,  // Allow more extra images
                isLoading = state.isLoadingImages
            )

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
                        // Launch coroutine to validate VIN
                        scope.launch {
                            try {
                                val vinValid = viewModel.validateVinBeforeSubmission()
                                if (vinValid) {
                                    viewModel.handleEvent(CheckScreenEvent.ShowFinishDialog)
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Error validating VIN")
                                Toast.makeText(context, "Error validating VIN: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, strings.fillRequiredFields, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid.value) DarkGreen else Color.Gray,
                    contentColor = Color.White
                ),
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
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = strings.sendingData ?: "Sending data...",
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Helper method to get model ID from name
private fun getModelIdFromName(carName: String): Int? {
    val normalizedName = carName.trim().uppercase()
    return when (normalizedName) {
        "BYD YUAN PLUS" -> 1
        "BYD TAN" -> 2
        "BYD YUAN PRO" -> 3
        "BYD SEAL" -> 4
        "BYD HAN" -> 5
        "BYD DOLPHIN PLUS" -> 6
        "BYD DOLPHIN" -> 7
        "BYD DOLPHIN MINI" -> 8
        "BYD SONG PRO DM-I" -> 9
        "SONG PLUS PREMIUM DM-I" -> 10
        "BYD SONG PLUS DM-I" -> 11
        "BYD KING DM-I" -> 12
        "BYD SHARK" -> 13
        else -> null
    }
}