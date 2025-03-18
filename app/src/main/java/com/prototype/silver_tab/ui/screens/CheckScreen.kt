package com.prototype.silver_tab.ui.screens

import ImageSection
import SectionCard
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.models.ImageDTO
import com.prototype.silver_tab.language.LocalStringResources
import com.prototype.silver_tab.ui.components.checkscreen.SuccessDialog
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.utils.getCarImageResource
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError
import com.prototype.silver_tab.viewmodels.CheckScreenViewModel

@Composable
fun CheckScreen(
    onSaveComplete: () -> Unit,
    viewModel: CheckScreenViewModel = hiltViewModel()
) {
    val tag = "CheckScreen"

    logTimber(tag, "Initiated CheckScreen")

    val strings = LocalStringResources.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Track which section we're currently adding an image to
    var currentImageSection by remember { mutableStateOf("") }

    val context = LocalContext.current

    // UI state
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()
    val selectedCar by viewModel.selectedCar.collectAsState()
    val isNewCar by viewModel.isNewCar.collectAsState()

    // Feature flags
    val needsBattery12vSection by viewModel.needsBattery12vSection.collectAsState()
    val needsHybridCheckSection by viewModel.needsHybridCheckSection.collectAsState()

    // Form values
    val vin by viewModel.vin.collectAsState()
    val socPercentage by viewModel.socPercentage.collectAsState()
    val battery12vVoltage by viewModel.battery12vVoltage.collectAsState()
    val fiveMinutesHybridCheck by viewModel.fiveMinutesHybridCheck.collectAsState()
    val tirePressureFrontRight by viewModel.tirePressureFrontRight.collectAsState()
    val tirePressureFrontLeft by viewModel.tirePressureFrontLeft.collectAsState()
    val tirePressureRearRight by viewModel.tirePressureRearRight.collectAsState()
    val tirePressureRearLeft by viewModel.tirePressureRearLeft.collectAsState()
    val comments by viewModel.comments.collectAsState()

    // Error states
    val vinError by viewModel.vinError.collectAsState()
    val socError by viewModel.socError.collectAsState()
    val batteryError by viewModel.batteryError.collectAsState()
    val tirePressureErrors by viewModel.tirePressureErrors.collectAsState()

    // Images
    val vinImages by viewModel.vinImages.collectAsState()
    val socImages by viewModel.socImages.collectAsState()
    val batteryImages by viewModel.batteryImages.collectAsState()
    val tireImages by viewModel.tireImages.collectAsState()
    val extraImages by viewModel.extraImages.collectAsState()

    // Focus Requesters
    val socFocusRequester = remember { FocusRequester() }
    val battery12vFocusRequester = remember { FocusRequester() }
    val frontLeftFocusRequester = remember { FocusRequester() }
    val frontRightFocusRequester = remember { FocusRequester() }
    val rearLeftFocusRequester = remember { FocusRequester() }
    val rearRightFocusRequester = remember { FocusRequester() }
    val commentsFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // UI dialog states
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Correction mode
    val isInCorrectionMode by viewModel.isInCorrectionMode.collectAsState()
    val isSuccessCorrection by viewModel.isSuccessCorrection.collectAsState()
    val originalVin by viewModel.originalVin.collectAsState()

    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(enabled = false) { /* Disable clicks */ }
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
//                    text = strings.savingData,
                    text = "Saving data...",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }

    // Show snackbar for errors
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Show success dialog and navigate back
    LaunchedEffect(success) {
        success?.let {
            showSuccessDialog = true
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header - Car info
            logTimber(tag, "Selected car: ${selectedCar?.name}")
            selectedCar?.let { car ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Car image
                        Image(
                            painter = painterResource(id = getCarImageResource(car.name)),
                            contentDescription = car.name,
                            modifier = Modifier
                                .size(120.dp)
                                .padding(8.dp)
                        )

                        // Car name
                        Text(
                            text = car.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        // Car type
                        Text(
                            text = car.type ?: "",
                            color = Color.LightGray,
                            fontSize = 16.sp
                        )

                        // New/update indicator
                        Text(
                            text = if (isNewCar) strings.newInspection else strings.updateInspection,
                            color = if (isNewCar) Color.Green else Color.Yellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            if (isInCorrectionMode) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE65100) // Deep orange color for correction mode
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Correction Mode",
                            tint = Color.White,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Column {
                            Text(
//                                text = strings.correctionMode,
                                text = "Correction Mode",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Text(
//                                text = strings.vinCannotBeChanged,
                                text = "Vin cant be changed bro",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // VIN Section
            SectionCard(
                title = strings.vin,
                helpType = "vin",
                helpImage = R.drawable.chassi,
                content = {
                    OutlinedTextField(
                        value = vin,
                        onValueChange = { viewModel.updateVin(it) },
                        label = { Text(strings.vin) },
                        isError = vinError != null,
                        supportingText = {
                            if (vinError != null) {
                                Text(vinError!!, color = Color.Red)
                            } else if (isInCorrectionMode) {
                                Text("VIN cannot be changed in correction mode", color = Color.Gray)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = if (isInCorrectionMode) Color.Gray else Color.White,
                            unfocusedTextColor = if (isInCorrectionMode) Color.Gray else Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledTextColor = Color.Gray,
                            disabledContainerColor = Color(0xFF333333)
                        ),
                        singleLine = true,
                        enabled = !isInCorrectionMode // Disable the field in correction mode
                    )

                    // VIN Images section
                    ImageSection(
                        title = strings.vinPhotos,
                        images = vinImages,
                        onAddImage = { uri ->
                            viewModel.processAndAddImage("vin", uri, context)
                        },
                        onRemoveImage = { viewModel.removeImage("vin", it) },
                        maxImages = 4
                    )
                }
            )

            // SOC Section
            SectionCard(
                title = strings.socPercentage,
                helpType = "soc",
                helpImage = R.drawable.soc_example,
                content = {
                    OutlinedTextField(
                        value = socPercentage,
                        onValueChange = { viewModel.updateSocPercentage(it) },
                        label = { Text(strings.enterSocPercentage) },
                        isError = socError != null,
                        supportingText = {
                            if (socError != null) {
                                Text(socError!!, color = Color.Red)
                            } else {
                                Text(strings.socPercentageRange, color = Color.Gray)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(socFocusRequester),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction =  ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (needsBattery12vSection) {
                                    battery12vFocusRequester.requestFocus()
                                } else {
                                    frontLeftFocusRequester.requestFocus()
                                }
                            },
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        singleLine = true
                    )

                    // SOC Images section
                    ImageSection(
                        title = strings.socPhotos,
                        images = socImages,
                        onAddImage = { uri ->
                            viewModel.processAndAddImage("soc", uri, context)
                        },
                        onRemoveImage = { viewModel.removeImage("soc", it) },
                        maxImages = 4
                    )
                }
            )

            // Battery 12V Section (only for specific models)
            if (needsBattery12vSection) {
                SectionCard(
                    title = strings.battery12v,
                    helpType = "battery",
                    helpImage = R.drawable.batteryhelpimage,
                    content = {
                        OutlinedTextField(
                            value = battery12vVoltage,
                            onValueChange = { viewModel.updateBattery12vVoltage(it) },
                            label = { Text(strings.enterBatteryVoltage) },
                            isError = batteryError != null,
                            supportingText = {
                                if (batteryError != null) {
                                    Text(batteryError!!, color = Color.Red)
                                } else {
                                    Text(strings.batteryVoltageRange, color = Color.Gray)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(battery12vFocusRequester),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    frontLeftFocusRequester.requestFocus()
                                }
                            ),
                            singleLine = true
                        )

                        // Battery Images section
                        ImageSection(
                            title = strings.batteryPhotos,
                            images = batteryImages,
                            onAddImage = { uri ->
                                viewModel.processAndAddImage("battery", uri, context)
                            },
                            onRemoveImage = { viewModel.removeImage("battery", it) },
                            maxImages = 4
                        )
                    }
                )
            }

            // Hybrid Check Section (only for hybrid models)
            if (needsHybridCheckSection) {
                SectionCard(
                    title = strings.hybridCarCheck,
                    content = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = fiveMinutesHybridCheck,
                                onCheckedChange = { viewModel.updateFiveMinutesHybridCheck(it) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Green,
                                    uncheckedColor = Color.Gray
                                )
                            )

                            Text(
                                text = strings.fiveMinutesHybridCheck,
                                color = Color.White,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                )
            }

            // Tire Pressure Section
            SectionCard(
                title = strings.tirePressure,
                helpType = "tire",
                helpImage = R.drawable.pneus,
                content = {
                    // Car diagram
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.car_draw),
                            contentDescription = "Car diagram",
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .aspectRatio(2f)
                        )
                    }

                    // Front tire pressures
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Front left
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = strings.frontLeft,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = tirePressureFrontLeft,
                                onValueChange = { viewModel.updateTirePressureFrontLeft(it) },
                                label = { Text(strings.psi) },
                                isError = tirePressureErrors.containsKey("frontLeft"),
                                supportingText = {
                                    if (tirePressureErrors.containsKey("frontLeft")) {
                                        Text(tirePressureErrors["frontLeft"]!!, color = Color.Red)
                                    }
                                },
                                modifier = Modifier
                                    .width(100.dp)
                                    .padding(top = 8.dp)
                                    .focusRequester(frontLeftFocusRequester),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        frontRightFocusRequester.requestFocus()
                                    }
                                ),
                                singleLine = true
                            )
                        }

                        // Front right
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = strings.frontRight,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = tirePressureFrontRight,
                                onValueChange = { viewModel.updateTirePressureFrontRight(it) },
                                label = { Text(strings.psi) },
                                isError = tirePressureErrors.containsKey("frontRight"),
                                supportingText = {
                                    if (tirePressureErrors.containsKey("frontRight")) {
                                        Text(tirePressureErrors["frontRight"]!!, color = Color.Red)
                                    }
                                },
                                modifier = Modifier
                                    .width(100.dp)
                                    .padding(top = 8.dp)
                                    .focusRequester(frontRightFocusRequester),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        rearLeftFocusRequester.requestFocus()
                                    }
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // Rear tire pressures
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Rear left
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = strings.rearLeft,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = tirePressureRearLeft,
                                onValueChange = { viewModel.updateTirePressureRearLeft(it) },
                                label = { Text(strings.psi) },
                                isError = tirePressureErrors.containsKey("rearLeft"),
                                supportingText = {
                                    if (tirePressureErrors.containsKey("rearLeft")) {
                                        Text(tirePressureErrors["rearLeft"]!!, color = Color.Red)
                                    }
                                },
                                modifier = Modifier
                                    .width(100.dp)
                                    .padding(top = 8.dp)
                                    .focusRequester(rearLeftFocusRequester),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        rearRightFocusRequester.requestFocus()
                                    }
                                ),
                                singleLine = true
                            )
                        }

                        // Rear right
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = strings.rearRight,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = tirePressureRearRight,
                                onValueChange = { viewModel.updateTirePressureRearRight(it) },
                                label = { Text(strings.psi) },
                                isError = tirePressureErrors.containsKey("rearRight"),
                                supportingText = {
                                    if (tirePressureErrors.containsKey("rearRight")) {
                                        Text(tirePressureErrors["rearRight"]!!, color = Color.Red)
                                    }
                                },
                                modifier = Modifier
                                    .width(100.dp)
                                    .padding(top = 8.dp)
                                    .focusRequester(rearRightFocusRequester),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        commentsFocusRequester.requestFocus()
                                    }
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // Tire Images section
                    ImageSection(
                        title = strings.tirePhotos,
                        images = tireImages,
                        onAddImage = { uri ->
                            viewModel.processAndAddImage("tire", uri, context)
                        },
                        onRemoveImage = { viewModel.removeImage("tire", it) },
                        maxImages = 4
                    )
                }
            )

            // Comments Section
            SectionCard(
                title = strings.comments,
                helpType = "comments",
                content = {
                    OutlinedTextField(
                        value = comments,
                        onValueChange = { viewModel.updateComments(it) },
                        label = { Text(strings.commentsOptional) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .focusRequester(commentsFocusRequester),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )
                    )

                    // Extra Images section
                    ImageSection(
                        title = strings.additionalPhotos,
                        images = extraImages,
                        onAddImage = { uri ->
                            viewModel.processAndAddImage("extra", uri, context)
                        },
                        onRemoveImage = { viewModel.removeImage("extra", it) },
                        maxImages = 4
                    )
                }
            )

            // Finalize button
            Button(
                onClick = {
                    logTimber(tag, "Saving PDI in mode ${viewModel.isSuccessCorrection}")
                    viewModel.savePdi()
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isInCorrectionMode) Color(0xFFE65100) else Color.Green, // Match the orange color
                    disabledContainerColor = Color.Gray
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = if (isInCorrectionMode) strings.updatePdi else strings.savePdi,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Add some bottom space
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Snackbar for errors
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            Snackbar(
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Text(data.visuals.message)
            }
        }

        // Success dialog
        if (showSuccessDialog) {
            SuccessDialog(
                show = true,
                message = success,
                vin = vin,
                isCorrection = isSuccessCorrection,
                onDismiss = {
                    showSuccessDialog = false
                    onSaveComplete()
                }
            )
        }
    }
}