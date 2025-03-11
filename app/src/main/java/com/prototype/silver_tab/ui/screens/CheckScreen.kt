@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.prototype.silver_tab.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prototype.silver_tab.R
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.SilverTabApplication.Companion.userPreferences
import retrofit2.HttpException
import com.prototype.silver_tab.data.api_connection.RetrofitClient
import com.prototype.silver_tab.data.models.CarResponse
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.PDI
import com.prototype.silver_tab.data.repository.ImageRepository
import com.prototype.silver_tab.ui.components.*

import com.prototype.silver_tab.ui.components.checkscreen.AdditionalInfoSection
import com.prototype.silver_tab.ui.components.checkscreen.CancelDialog
import com.prototype.silver_tab.ui.components.checkscreen.FinishDialog
import com.prototype.silver_tab.ui.components.checkscreen.HybridCarSection
import com.prototype.silver_tab.ui.components.checkscreen.ImageType
import com.prototype.silver_tab.ui.components.checkscreen.VehicleInfoCard
import com.prototype.silver_tab.ui.components.checkscreen.rememberCameraManager
import com.prototype.silver_tab.ui.components.help.HelpButton
import com.prototype.silver_tab.ui.components.help.HelpModal
import com.prototype.silver_tab.ui.dialogs.*
import com.prototype.silver_tab.ui.components.help.*
import com.prototype.silver_tab.ui.theme.DarkGreen

import com.prototype.silver_tab.ui.components.checkscreen.*

import com.prototype.silver_tab.utils.CameraUtils
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.isValidVIN
import com.prototype.silver_tab.viewmodels.CheckScreenState
import com.prototype.silver_tab.viewmodels.CheckScreenViewModel
import com.prototype.silver_tab.viewmodels.DealerViewModel
import com.prototype.silver_tab.viewmodels.SharedCarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun Section(
    title: String,
    modifier: Modifier = Modifier,
    showHelpModal: Boolean,
    showHelpIcon: Boolean = true,
    onShowHelpModalChange: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(vertical = 16.dp)) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            if (showHelpIcon) {
                HelpButton(onClick = { onShowHelpModalChange(true) })
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun CheckScreen(
    viewModel: CheckScreenViewModel = viewModel(),
    selectedInspectionInfo: InspectionInfo?,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    sharedCarViewModel: SharedCarViewModel = viewModel(),
    dealerViewModel: DealerViewModel = viewModel()
) {
    val strings = LocalStringResources.current
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val cameraUtils = remember { CameraUtils(context) }
    val pdiList by sharedCarViewModel.listHistoricCars.collectAsState()

    // Focus and keyboard managers
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Creating FocusRequesters for each field
    val frontLeftFocusRequester = remember { FocusRequester() }
    val frontRightFocusRequester = remember { FocusRequester() }
    val rearLeftFocusRequester = remember { FocusRequester() }
    val rearRightFocusRequester = remember { FocusRequester() }

    // Error states
    var chassisError by remember { mutableStateOf(false) }
    var socError by remember { mutableStateOf(false) }
    var frontLeftError by remember { mutableStateOf(false) }
    var frontRightError by remember { mutableStateOf(false) }
    var rearLeftError by remember { mutableStateOf(false) }
    var rearRightError by remember { mutableStateOf(false) }
    var batteryVoltageError by remember { mutableStateOf(false) }
    var showValidationErrorDialog by remember { mutableStateOf(false) }

    var modelo by remember { mutableStateOf("") }
    // Help modal states
    var showHelpModalChassi by remember { mutableStateOf(false) }
    var showHelpModalSoc by remember { mutableStateOf(false) }
    var showHelpModal12VBateria by remember { mutableStateOf(false) }
    var showHelpModalPneus by remember { mutableStateOf(false) }
    var showHelpModalHybrid by remember { mutableStateOf(false) }
    var showHelpModalInfo by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }

    val allDealerCars by sharedCarViewModel.listAllDealerCars.collectAsState()
    var showDuplicateVinDialog by remember { mutableStateOf(false) }

    // Determine if 12V battery is required
    val requireBattery12V = selectedInspectionInfo?.name == "BYD DOLPHIN MINI" ||
            selectedInspectionInfo?.name == "BYD YUAN PLUS"

    // Get dealer selected by user
    val selectedDealer by dealerViewModel.selectedDealer.collectAsState()
    val dealerCodeUser = selectedDealer?.dealerCode ?: "DEFAULT_CODE"

    // Function to validate form
    fun validateForm(): Boolean {
        var isValid = true

        if (state.chassisNumber.isBlank()) {
            chassisError = true
            isValid = false
        }

        if (state.socPercentage.isBlank()) {
            socError = true
            isValid = false
        }

        if (state.frontLeftPressure.isBlank()) {
            frontLeftError = true
            isValid = false
        }

        if (state.frontRightPressure.isBlank()) {
            frontRightError = true
            isValid = false
        }

        if (state.rearLeftPressure.isBlank()) {
            rearLeftError = true
            isValid = false
        }

        if (state.rearRightPressure.isBlank()) {
            rearRightError = true
            isValid = false
        }

        if (requireBattery12V && state.batteryVoltage.isBlank()) {
            batteryVoltageError = true
            isValid = false
        }

        return isValid
    }

    // Help modals
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
            type = "SOC",
            strings = strings
        )
    }

    if(showHelpModalPneus){
        HelpModal(
            onDismiss = { showHelpModalPneus = false },
            img = R.drawable.pneus,
            type = strings.tirePressure,
            strings = strings
        )
    }

    if(showHelpModal12VBateria){
        HelpModal(
            onDismiss = { showHelpModal12VBateria = false },
            img = R.drawable.batteryhelpimage,
            type = strings.batteryVoltage,
            strings = strings
        )
    }

    if(showHelpModalHybrid){
        HelpModal(
            onDismiss = { showHelpModalHybrid = false },
            img = null,
            type = "híbrido",
            strings = strings
        )
    }

    if(showHelpModalInfo){
        HelpModal(
            onDismiss = { showHelpModalInfo = false },
            img = 0,
            type = "",
            strings = strings
        )
    }

    // Validation error dialog
    if(showValidationErrorDialog) {
        AlertDialog(
            onDismissRequest = { showValidationErrorDialog = false },
            title = { Text(text = strings.errorTitle ?: "Erro de Validação") },
            text = { Text(text = strings.fillRequiredFields ?: "Por favor, preencha todos os campos obrigatórios.") },
            confirmButton = {
                Button(
                    onClick = { showValidationErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(strings.understood ?: "Entendi")
                }
            }
        )
    }

    LaunchedEffect(selectedInspectionInfo) {
        selectedInspectionInfo?.let { car ->
            viewModel.initializeWithCar(car)
            modelo = car.name ?: ""
        }
    }

    val cameraState = rememberCameraManager(
        context = context,
        cameraUtils = cameraUtils,
        onImageCaptured = viewModel::addImage
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            VehicleInfoCard(selectedInspectionInfo = selectedInspectionInfo)

            // Chassis section with required field shown immediately when empty
            Section(
                title = strings.chassisNumber,
                showHelpModal = showHelpModalChassi,
                onShowHelpModalChange = {showHelpModalChassi = it}
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.chassisNumber,
                        onValueChange = { newValue ->
                            if (newValue.isBlank()) {
                                viewModel.updateChassisNumber(newValue)
                                chassisError = true
                            } else {
                                viewModel.updateChassisNumber(newValue)
                                chassisError = false
//                                 Validate VIN: must be 17 characters and pass the check digit test.
//                                chassisError = newValue.length != 17 || !isValidVIN(newValue)
                            }
                        },
                        label = { Text(text = strings.chassisNumber, color = Color.White) },
                        isError = chassisError,
                        supportingText = {
                            when {
                                state.chassisNumber.isBlank() ->
                                    Text(
                                        text = strings.neededField ?: "Campo obrigatório",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                state.chassisNumber.isNotBlank() && state.chassisNumber.length != 17 ->
                                    Text(
                                        text = "VIN deve conter 17 caracteres.",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                chassisError ->
                                    Text(
                                        text = "VIN inválido.",
                                        color = MaterialTheme.colorScheme.error
                                    )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedLabelColor = if (chassisError) MaterialTheme.colorScheme.error else Color.Gray,
                            unfocusedLabelColor = if (chassisError) MaterialTheme.colorScheme.error else Color.Gray,
                            focusedIndicatorColor = if (chassisError) MaterialTheme.colorScheme.error else Color.Gray,
                            unfocusedIndicatorColor = if (chassisError) MaterialTheme.colorScheme.error else Color.Gray,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray,
                            errorIndicatorColor = MaterialTheme.colorScheme.error,
                            errorLabelColor = MaterialTheme.colorScheme.error
                        )
                    )

                }

                ImageUploadField(
                    title = strings.chassisPhoto,
                    imageUris = state.chassisImageUris,
                    onCameraClick = { cameraState.launchCamera(ImageType.CHASSIS) },
                    onGalleryClick = { cameraState.launchGallery(ImageType.CHASSIS) },
                    onDeleteImage = { index -> viewModel.removeImage(ImageType.CHASSIS, index) },
                    strings = strings
                )
            }

            // SOC section (unchanged – already shows required text when empty)
            Section(
                title = "SOC",
                showHelpModal = showHelpModalSoc,
                onShowHelpModalChange = {showHelpModalSoc = it}
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.socPercentage,
                        onValueChange = { newValue ->
                            if (newValue.isBlank()) {
                                viewModel.updateSocPercentage(newValue)
                                socError = true
                                return@OutlinedTextField
                            }
                            val processedValue = newValue.replace(',', '.').filter { it.isDigit() || it == '.' }
                            val validatedValue = if (processedValue.count { it == '.' } > 1) {
                                val firstDotIndex = processedValue.indexOf('.')
                                processedValue.substring(0, firstDotIndex + 1) +
                                        processedValue.substring(firstDotIndex + 1).replace(".", "")
                            } else {
                                processedValue
                            }
                            // Ensure at most two decimal places
                            val formattedValue = validatedValue.split('.').let { parts ->
                                if (parts.size == 2 && parts[1].length > 2) {
                                    "${parts[0]}.${parts[1].take(2)}"
                                } else {
                                    validatedValue
                                }
                            }
                            val numericValue = formattedValue.toDoubleOrNull()
                            socError = numericValue == null || numericValue > 100.0
                            viewModel.updateSocPercentage(formattedValue)
                        },
                        label = { Text(text = strings.socPercentage, color = Color.White) },
                        isError = socError,
                        supportingText = {
                            when {
                                state.socPercentage.isBlank() -> Text(
                                    text = strings.neededField ?: "Campo obrigatório",
                                    color = MaterialTheme.colorScheme.error
                                )
                                socError -> Text(
                                    text = strings.socValueWarning,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedLabelColor = if (socError) MaterialTheme.colorScheme.error else Color.Gray,
                            unfocusedLabelColor = if (socError) MaterialTheme.colorScheme.error else Color.Gray,
                            focusedIndicatorColor = if (socError) MaterialTheme.colorScheme.error else Color.Gray,
                            unfocusedIndicatorColor = if (socError) MaterialTheme.colorScheme.error else Color.Gray,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray,
                            errorIndicatorColor = MaterialTheme.colorScheme.error,
                            errorLabelColor = MaterialTheme.colorScheme.error
                        )
                    )
                }

                ImageUploadField(
                    title = strings.batteryPhoto,
                    imageUris = state.socImageUris,
                    onCameraClick = { cameraState.launchCamera(ImageType.SOC) },
                    onGalleryClick = { cameraState.launchGallery(ImageType.SOC) },
                    onDeleteImage = { index -> viewModel.removeImage(ImageType.SOC, index) },
                    strings = strings
                )
            }

            // Tire pressure section with only numbers below 50 accepted and required text shown when empty
            Section(
                title = strings.tirePressure,
                showHelpModal = showHelpModalPneus,
                onShowHelpModalChange = {showHelpModalPneus = it},
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Front Left Tire Pressure Field ("DE")
                        OutlinedTextField(
                            value = state.frontLeftPressure,
                            onValueChange = { newValue ->
                                if (newValue.isBlank()) {
                                    viewModel.updateFrontLeftPressure(newValue)
                                    frontLeftError = true
                                } else {
                                    val processedValue = newValue.replace(',', '.').filter { it.isDigit() || it == '.' }
                                    val validatedValue = if (processedValue.count { it == '.' } > 1) {
                                        val firstDotIndex = processedValue.indexOf('.')
                                        processedValue.substring(0, firstDotIndex + 1) +
                                                processedValue.substring(firstDotIndex + 1).replace(".", "")
                                    } else {
                                        processedValue
                                    }
                                    val numericValue = validatedValue.toDoubleOrNull()
                                    if (numericValue != null && numericValue < 50.0) {
                                        viewModel.updateFrontLeftPressure(validatedValue)
                                        frontLeftError = false
                                    } else {
                                        frontLeftError = true
                                    }
                                }
                            },
                            label = { Text("DE", color = if (frontLeftError) MaterialTheme.colorScheme.error else Color.White) },
                            isError = frontLeftError,
                            supportingText = {
                                if (state.frontLeftPressure.isBlank()) {
                                    Text(text = strings.neededField ?: "Campo obrigatório", color = MaterialTheme.colorScheme.error)
                                } else if (frontLeftError) {
                                    Text(text = strings.tireWarning, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
                                .focusRequester(frontLeftFocusRequester),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedLabelColor = if (frontLeftError) MaterialTheme.colorScheme.error else Color.Gray,
                                unfocusedLabelColor = if (frontLeftError) MaterialTheme.colorScheme.error else Color.Gray,
                                focusedIndicatorColor = if (frontLeftError) MaterialTheme.colorScheme.error else Color.Gray,
                                unfocusedIndicatorColor = if (frontLeftError) MaterialTheme.colorScheme.error else Color.Gray,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray,
                                errorIndicatorColor = MaterialTheme.colorScheme.error,
                                errorLabelColor = MaterialTheme.colorScheme.error
                            )
                        )


                        // Front Right Tire Pressure Field ("DD")
                        OutlinedTextField(
                            value = state.frontRightPressure,
                            onValueChange = { newValue ->
                                if (newValue.isBlank()) {
                                    viewModel.updateFrontRightPressure(newValue)
                                    frontRightError = true
                                } else {
                                    val processedValue = newValue.replace(',', '.').filter { it.isDigit() || it == '.' }
                                    val validatedValue = if (processedValue.count { it == '.' } > 1) {
                                        val firstDotIndex = processedValue.indexOf('.')
                                        processedValue.substring(0, firstDotIndex + 1) +
                                                processedValue.substring(firstDotIndex + 1).replace(".", "")
                                    } else {
                                        processedValue
                                    }
                                    val numericValue = validatedValue.toDoubleOrNull()
                                    if (numericValue != null && numericValue < 50.0) {
                                        viewModel.updateFrontRightPressure(validatedValue)
                                        frontRightError = false
                                    } else {
                                        frontRightError = true
                                    }
                                }
                            },
                            label = { Text("DD", color = if (frontRightError) MaterialTheme.colorScheme.error else Color.White) },
                            isError = frontRightError,
                            supportingText = {
                                if (state.frontRightPressure.isBlank()) {
                                    Text(text = strings.neededField ?: "Campo obrigatório", color = MaterialTheme.colorScheme.error)
                                } else if (frontRightError) {
                                    Text(text = strings.tireWarning, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                                .focusRequester(frontRightFocusRequester),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedLabelColor = if (frontRightError) MaterialTheme.colorScheme.error else Color.Gray,
                                unfocusedLabelColor = if (frontRightError) MaterialTheme.colorScheme.error else Color.Gray,
                                focusedIndicatorColor = if (frontRightError) MaterialTheme.colorScheme.error else Color.Gray,
                                unfocusedIndicatorColor = if (frontRightError) MaterialTheme.colorScheme.error else Color.Gray,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray,
                                errorIndicatorColor = MaterialTheme.colorScheme.error,
                                errorLabelColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Rear Left Tire Pressure Field ("TE")
                        OutlinedTextField(
                            value = state.rearLeftPressure,
                            onValueChange = { newValue ->
                                if (newValue.isBlank()) {
                                    viewModel.updateRearLeftPressure(newValue)
                                    rearLeftError = true
                                } else {
                                    val processedValue = newValue.replace(',', '.').filter { it.isDigit() || it == '.' }
                                    val validatedValue = if (processedValue.count { it == '.' } > 1) {
                                        val firstDotIndex = processedValue.indexOf('.')
                                        processedValue.substring(0, firstDotIndex + 1) +
                                                processedValue.substring(firstDotIndex + 1).replace(".", "")
                                    } else {
                                        processedValue
                                    }
                                    val numericValue = validatedValue.toDoubleOrNull()
                                    if (numericValue != null && numericValue < 50.0) {
                                        viewModel.updateRearLeftPressure(validatedValue)
                                        rearLeftError = false
                                    } else {
                                        rearLeftError = true
                                    }
                                }
                            },
                            label = { Text("TE", color = if (rearLeftError) MaterialTheme.colorScheme.error else Color.White) },
                            isError = rearLeftError,
                            supportingText = {
                                if (state.rearLeftPressure.isBlank()) {
                                    Text(text = strings.neededField ?: "Campo obrigatório", color = MaterialTheme.colorScheme.error)
                                } else if (rearLeftError) {
                                    Text(text = strings.tireWarning, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
                                .focusRequester(rearLeftFocusRequester),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedLabelColor = if (rearLeftError) MaterialTheme.colorScheme.error else Color.Gray,
                                unfocusedLabelColor = if (rearLeftError) MaterialTheme.colorScheme.error else Color.Gray,
                                focusedIndicatorColor = if (rearLeftError) MaterialTheme.colorScheme.error else Color.Gray,
                                unfocusedIndicatorColor = if (rearLeftError) MaterialTheme.colorScheme.error else Color.Gray,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray,
                                errorIndicatorColor = MaterialTheme.colorScheme.error,
                                errorLabelColor = MaterialTheme.colorScheme.error
                            )
                        )

                        // Rear Right Tire Pressure Field ("TD")
                        OutlinedTextField(
                            value = state.rearRightPressure,
                            onValueChange = { newValue ->
                                if (newValue.isBlank()) {
                                    viewModel.updateRearRightPressure(newValue)
                                    rearRightError = true
                                } else {
                                    val processedValue = newValue.replace(',', '.').filter { it.isDigit() || it == '.' }
                                    val validatedValue = if (processedValue.count { it == '.' } > 1) {
                                        val firstDotIndex = processedValue.indexOf('.')
                                        processedValue.substring(0, firstDotIndex + 1) +
                                                processedValue.substring(firstDotIndex + 1).replace(".", "")
                                    } else {
                                        processedValue
                                    }
                                    val numericValue = validatedValue.toDoubleOrNull()
                                    if (numericValue != null && numericValue < 50.0) {
                                        viewModel.updateRearRightPressure(validatedValue)
                                        rearRightError = false
                                    } else {
                                        rearRightError = true
                                    }
                                }
                            },
                            label = { Text("TD", color = if (rearRightError) MaterialTheme.colorScheme.error else Color.White) },
                            isError = rearRightError,
                            supportingText = {
                                if (state.rearRightPressure.isBlank()) {
                                    Text(text = strings.neededField ?: "Campo obrigatório", color = MaterialTheme.colorScheme.error)
                                } else if (rearRightError) {
                                    Text(text = strings.tireWarning, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                                .focusRequester(rearRightFocusRequester),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedLabelColor = if (rearRightError) MaterialTheme.colorScheme.error else Color.Gray,
                                unfocusedLabelColor = if (rearRightError) MaterialTheme.colorScheme.error else Color.Gray,
                                focusedIndicatorColor = if (rearRightError) MaterialTheme.colorScheme.error else Color.Gray,
                                unfocusedIndicatorColor = if (rearRightError) MaterialTheme.colorScheme.error else Color.Gray,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray,
                                errorIndicatorColor = MaterialTheme.colorScheme.error,
                                errorLabelColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }

                ImageUploadField(
                    title = strings.tirePressurePhoto,
                    imageUris = state.tirePressureImageUris,
                    onCameraClick = { cameraState.launchCamera(ImageType.TIRE_PRESSURE) },
                    onGalleryClick = { cameraState.launchGallery(ImageType.TIRE_PRESSURE) },
                    onDeleteImage = { index -> viewModel.removeImage(ImageType.TIRE_PRESSURE, index) },
                    strings = strings
                )
            }

            // Hybrid car section
            if (selectedInspectionInfo?.type?.contains("Híbrido", ignoreCase = true) == true) {
                Section(
                    title = strings.vehicleTypeHybrid,
                    showHelpModal = showHelpModalHybrid,
                    onShowHelpModalChange = {showHelpModalHybrid = it}
                ) {
                    HybridCarSection(
                        isCarStarted = state.isCarStarted,
                        onCarStartedChange = viewModel::updateCarStarted
                    )
                }
            }

            // 12V Battery Section
            if (requireBattery12V) {
                Section(
                    title = strings.batteryVoltage,
                    showHelpModal = false,
                    onShowHelpModalChange = {showHelpModal12VBateria = it}
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = state.batteryVoltage,
                            onValueChange = { newValue ->
                                if (newValue.isBlank()) {
                                    viewModel.updateBatteryVoltage(newValue)
                                    socError = true
                                    return@OutlinedTextField
                                }
                                val processedValue = newValue.replace(',', '.').filter { it.isDigit() || it == '.' }
                                val validatedValue = if (processedValue.count { it == '.' } > 1) {
                                    val firstDotIndex = processedValue.indexOf('.')
                                    processedValue.substring(0, firstDotIndex + 1) +
                                            processedValue.substring(firstDotIndex + 1).replace(".", "")
                                } else {
                                    processedValue
                                }
                                // Ensure at most two decimal places
                                val formattedValue = validatedValue.split('.').let { parts ->
                                    if (parts.size == 2 && parts[1].length > 2) {
                                        "${parts[0]}.${parts[1].take(2)}"
                                    } else {
                                        validatedValue
                                    }
                                }
                                val numericValue = formattedValue.toDoubleOrNull()
                                batteryVoltageError = numericValue == null || numericValue > 12.0
                                viewModel.updateBatteryVoltage(formattedValue)
                            },
                            label = { Text(text = strings.batteryVoltage, color = Color.White) },
                            isError = batteryVoltageError,
                            supportingText = {
                                if (state.batteryVoltage.isBlank()) {
                                    Text(
                                        text = strings.neededField ?: "Campo obrigatório",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedLabelColor = if (batteryVoltageError) MaterialTheme.colorScheme.error else Color.Gray,
                                unfocusedLabelColor = if (batteryVoltageError) MaterialTheme.colorScheme.error else Color.Gray,
                                focusedIndicatorColor = if (batteryVoltageError) MaterialTheme.colorScheme.error else Color.Gray,
                                unfocusedIndicatorColor = if (batteryVoltageError) MaterialTheme.colorScheme.error else Color.Gray,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray,
                                errorIndicatorColor = MaterialTheme.colorScheme.error,
                                errorLabelColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }

                    ImageUploadField(
                        title = strings.batteryPhoto,
                        imageUris = state.battery12VImageUris,
                        onCameraClick = { cameraState.launchCamera(ImageType.BATTERY_12VOLTAGE) },
                        onGalleryClick = { cameraState.launchGallery(ImageType.BATTERY_12VOLTAGE) },
                        onDeleteImage = { index -> viewModel.removeImage(ImageType.BATTERY_12VOLTAGE, index) },
                        strings = strings
                    )
                }
            }

            // Additional info section
            Section(
                title = strings.additionalInfo,
                showHelpModal = false,
                showHelpIcon = false,
                onShowHelpModalChange = {}
            ) {
                AdditionalInfoSection(
                    additionalInfo = state.additionalInfo,
                    onAdditionalInfoChange = viewModel::updateAdditionalInfo
                )

                ImageUploadField(
                    title = strings.extraImages,
                    imageUris = state.extraImageUris,
                    onCameraClick = { cameraState.launchCamera(ImageType.EXTRA_IMAGE) },
                    onGalleryClick = { cameraState.launchGallery(ImageType.EXTRA_IMAGE) },
                    onDeleteImage = { index -> viewModel.removeImage(ImageType.EXTRA_IMAGE, index) },
                    strings = strings
                )
            }

            Button(
                onClick = {
                    if (validateForm()) {
                        viewModel.showFinishDialog()
                    } else {
                        showValidationErrorDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen,
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

        val userId by remember {
            SilverTabApplication.authRepository.authState.map { it?.userId ?: 0L }
        }.collectAsState(initial = 0L)

        SuccessDialog(
            show = state.showSuccessDialog,
            onDismiss = {
                viewModel.hideSuccessDialog()
                onFinish()
            },
            chassiNumber = state.chassisNumber,
            strings = strings
        )

        FinishDialog(
            show = state.showFinishDialog,
            onDismiss = viewModel::hideFinishDialog,
            onConfirm = {
                viewModel.hideFinishDialog()
                val vinAlreadyExists = allDealerCars.any { car ->
                    car.vin.equals(state.chassisNumber, ignoreCase = true)
                }

                if (vinAlreadyExists) {
                    showDuplicateVinDialog = true
                } else {
                    isSubmitting = true
                    viewModel.viewModelScope.launch {
                        try {
                            if (pdiList.none { it.chassi == state.chassisNumber }) {
                                val model_id = getCarModelId(modelo)
                                val car_id = postCarRequest(
                                    state = state,
                                    context = context,
                                    modelo = model_id,
                                    dealerCodeUser = dealerCodeUser
                                )

                                val pdi_id = postPdiRequest(
                                    state = state,
                                    context = context,
                                    car_id = car_id,
                                    userId = userId,
                                    dealerCodeUser = dealerCodeUser
                                )

                                pdi_id?.let { pdiId ->
                                    if (state.chassisImageUris.isNotEmpty()) {
                                        ImageRepository.uploadImages(
                                            context = context,
                                            pdiId = pdi_id,
                                            uris = state.chassisImageUris,
                                            imageType = "vin"
                                        )
                                    }

                                    if (state.socImageUris.isNotEmpty()) {
                                        ImageRepository.uploadImages(
                                            context = context,
                                            pdiId = pdi_id,
                                            uris = state.socImageUris,
                                            imageType = "soc"
                                        )
                                    }

                                    if (state.battery12VImageUris.isNotEmpty()) {
                                        ImageRepository.uploadImages(
                                            context = context,
                                            pdiId = pdi_id,
                                            uris = state.battery12VImageUris,
                                            imageType = "battery12V"
                                        )
                                    }

                                    if (state.tirePressureImageUris.isNotEmpty()) {
                                        ImageRepository.uploadImages(
                                            context = context,
                                            pdiId = pdi_id,
                                            uris = state.tirePressureImageUris,
                                            imageType = "tire"
                                        )
                                    }

                                    if (state.extraImageUris.isNotEmpty()) {
                                        ImageRepository.uploadImages(
                                            context = context,
                                            pdiId = pdi_id,
                                            uris = state.extraImageUris,
                                            imageType = "extraImages"
                                        )
                                    }
                                }
                            } else {
                                val car_id = getCarIdByChassi(state.chassisNumber)
                                val pdi_id = postPdiRequest(
                                    state = state,
                                    context = context,
                                    userId = userId,
                                    car_id = car_id,
                                    dealerCodeUser = dealerCodeUser
                                )

                                pdi_id?.let { pdiId ->
                                    if (state.chassisImageUris.isNotEmpty()) {
                                        ImageRepository.uploadImages(
                                            context = context,
                                            pdiId = pdi_id,
                                            uris = state.chassisImageUris,
                                            imageType = "vin"
                                        )
                                    }

                                    if (state.socImageUris.isNotEmpty()) {
                                        ImageRepository.uploadImages(
                                            context = context,
                                            pdiId = pdi_id,
                                            uris = state.socImageUris,
                                            imageType = "soc"
                                        )
                                    }

                                    if (state.battery12VImageUris.isNotEmpty()) {
                                        ImageRepository.uploadImages(
                                            context = context,
                                            pdiId = pdi_id,
                                            uris = state.battery12VImageUris,
                                            imageType = "battery12V"
                                        )
                                    }

                                    if (state.tirePressureImageUris.isNotEmpty()) {
                                        ImageRepository.uploadImages(
                                            context = context,
                                            pdiId = pdi_id,
                                            uris = state.tirePressureImageUris,
                                            imageType = "tire"
                                        )
                                    }

                                    if (state.extraImageUris.isNotEmpty()) {
                                        ImageRepository.uploadImages(
                                            context = context,
                                            pdiId = pdi_id,
                                            uris = state.extraImageUris,
                                            imageType = "extraImages"
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("CheckScreen", "Error submitting PDI: ${e.message}", e)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } finally {
                            isSubmitting = false
                            viewModel.showSuccessDialog()
                        }
                    }
                }
            },
            strings = strings
        )
    }

    if (isSubmitting) {
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
                    text = strings.sendingData ?: "Enviando PDI...",
                    color = Color.White
                )
            }
        }
    }

    if (showDuplicateVinDialog) {
        AlertDialog(
            onDismissRequest = { showDuplicateVinDialog = false },
            title = { Text(
                text = strings.duplicateVin,
                textAlign = TextAlign.Center
            ) },
            text = {
                Text(
                    strings.duplicateVinMessage,
                )
            },
            confirmButton = {
                Button(
                    onClick = { showDuplicateVinDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(strings.understood)
                }
            }
        )
    }
}

private suspend fun getCarIdByChassi(chassi: String): Int? {
    return try {
        val response = withContext(Dispatchers.IO) {
            RetrofitClient.carsApi.getCarId(chassi)
        }
        response.car_id
    } catch (e: Exception) {
        Timber.e(e, "Erro ao buscar car_id para chassi: $chassi")
        //saveLogToFile("Erro ao buscar car_id: ${e.message}")
        null
    }
}

private suspend fun postPdiRequest(
    state: CheckScreenState,
    context: Context,
    car_id: Int? = null,
    userId: Long? = null,
    dealerCodeUser: String? = null
): Int? {
    val inspectionDate = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    val formattedDate = inspectionDate.format(formatter)

    val pdi = PDI(
        pdi_id = null,
        car_id = car_id,
        create_by_user_id = userId?.toInt(),
        created_date = formattedDate,
        soc_percentage = state.socPercentage.toDoubleOrNull() ?: 0.0,
        battery12v_Voltage = state.batteryVoltage.toDoubleOrNull() ?: 58.0,
        tire_pressure_dd = state.frontRightPressure.toDoubleOrNull() ?: 0.0,
        tire_pressure_de = state.frontLeftPressure.toDoubleOrNull() ?: 0.0,
        tire_pressure_td = state.rearRightPressure.toDoubleOrNull() ?: 0.0,
        tire_pressure_te = state.rearLeftPressure.toDoubleOrNull() ?: 0.0,
        five_minutes_hybrid_check = state.isCarStarted,
        user_comments = state.additionalInfo
    )

    Log.d("PDI_DEBUG", "PDI a ser enviado:\n${pdi}")

    return try {
        val response = withContext(Dispatchers.IO) {
            RetrofitClient.pdiApi.postPdi(pdi)
        }
        if (response.isSuccessful) {
            val created_pdi = response.body()
            created_pdi?.pdi_id
        } else {
            val errorBody = response.errorBody()?.string()
            Timber.e("Erro na resposta do postPdiRequest: $errorBody")
            //saveLogToFile("Erro no postPdiRequest: $errorBody")
            null
        }
    } catch (e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        Timber.e(e, "Erro HTTP ao enviar PDI")
        null
    } catch (e: IOException) {
        Timber.e(e, "Erro de rede ao enviar PDI")
        null
    } catch (e: Exception) {
        Timber.e(e, "Erro inesperado ao enviar PDI")
        null
    }
}

private suspend fun postCarRequest(
    state: CheckScreenState,
    context: Context,
    modelo: Int?,
    dealerCodeUser: String
): Int? {
    val car = CarResponse(
        car_id = null,
        car_model_id = modelo,
        dealer_code = dealerCodeUser,
        vin = state.chassisNumber,
        pdi_ids = null,
        is_sold = false
    )
    Log.d("PDI_DEBUG", "Car a ser enviado:\n${car}")

    return try {
        val response = withContext(Dispatchers.IO) {
            RetrofitClient.carsApi.postCar(car)
        }
        if (response.isSuccessful) {
            val createdCar = response.body()
            Log.d("postCarRequest", "Car enviado com sucesso! car_id: ${createdCar?.car_id}")
            createdCar?.car_id
        } else {
            val errorBody = response.errorBody()?.string()
            Timber.e("Erro na resposta do postCarRequest: $errorBody")
            null
        }
    } catch (e: Exception) {
        Timber.e(e, "Erro inesperado ao cadastrar carro")
        null
    }
}

fun getCarModelId(modelName: String): Int? {
    val carModels = mapOf(
        "BYD YUAN PLUS" to 1,
        "BYD TAN" to 2,
        "BYD YUAN PRO" to 3,
        "BYD SEAL" to 4,
        "BYD HAN" to 5,
        "BYD DOLPHIN PLUS" to 6,
        "BYD DOLPHIN" to 7,
        "BYD DOLPHIN MINI" to 8,
        "BYD SONG PRO DM-i" to 9,
        "SONG PLUS PREMIUM DM-i" to 10,
        "BYD SONG PLUS DM-i" to 11,
        "BYD KING DM-i" to 12,
        "BYD SHARK" to 13
    )
    return carModels[modelName]
}