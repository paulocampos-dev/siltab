package com.prototype.silver_tab.ui.components.checkscreen

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.ui.components.ImageUploadField
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.StringResources

/**
 * Chassis Section for PDI Check Screen
 */
@Composable
fun ChassisSection(
    chassisNumber: String,
    onChassisNumberChange: (String) -> Unit,
    chassisError: Boolean,
    chassisImageUris: List<Uri>,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDeleteImage: (Int) -> Unit,
    showHelpModal: Boolean,
    onShowHelpModalChange: (Boolean) -> Unit,
    strings: StringResources,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Section(
        title = strings.chassisNumber,
        showHelpModal = showHelpModal,
        onShowHelpModalChange = onShowHelpModalChange
    ) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = chassisNumber,
                onValueChange = onChassisNumberChange,
                label = { Text(text = strings.chassisNumber, color = Color.White) },
                isError = chassisError,
                enabled = enabled,
                supportingText = {
                    if (!enabled) {
                        Text(text = "VIN from existing car cannot be changed", color = Color.Gray)
                    } else {
                        when {
                            chassisNumber.isBlank() ->
                                Text(
                                    text = strings.neededField ?: "Campo obrigatório",
                                    color = MaterialTheme.colorScheme.error
                                )
                            chassisNumber.isNotBlank() && chassisNumber.length != 17 ->
                                Text(
                                    text = "VIN deve conter 17 caracteres.",
                                    color = MaterialTheme.colorScheme.error
                                )
                            chassisError ->
                                Text(
                                    text = "VIN inválido.",
                                    color = MaterialTheme.colorScheme.error
                                )
                            else -> {}
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = if (!enabled) Color.Gray else Color.White,
                    unfocusedTextColor = if (!enabled) Color.Gray else Color.White,
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
            imageUris = chassisImageUris,
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick,
            onDeleteImage = onDeleteImage,
            strings = strings,
            isLoading = isLoading
        )
    }
}

/**
 * SOC Section for PDI Check Screen
 */
@Composable
fun SocSection(
    socPercentage: String,
    onSocPercentageChange: (String) -> Unit,
    socError: Boolean,
    socImageUris: List<Uri>,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDeleteImage: (Int) -> Unit,
    showHelpModal: Boolean,
    onShowHelpModalChange: (Boolean) -> Unit,
    strings: StringResources,
    isLoading: Boolean = false
) {
    Section(
        title = "SOC",
        showHelpModal = showHelpModal,
        onShowHelpModalChange = onShowHelpModalChange
    ) {
        Row(modifier = Modifier.padding(bottom = 8.dp)) {
            OutlinedTextField(
                value = socPercentage,
                onValueChange = onSocPercentageChange,
                label = { Text(text = strings.socPercentage, color = Color.White) },
                isError = socError,
                supportingText = {
                    when {
                        socPercentage.isBlank() -> Text(
                            text = strings.neededField ?: "Campo obrigatório",
                            color = MaterialTheme.colorScheme.error
                        )
                        socError -> Text(
                            text = strings.socValueWarning,
                            color = MaterialTheme.colorScheme.error
                        )
                        else -> {}
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
            title = strings.batteryPhoto, // Reuse the original title if intended
            imageUris = socImageUris,
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick,
            onDeleteImage = onDeleteImage,
            strings = strings,
            isLoading = isLoading
        )
    }
}

/**
 * Tire Pressure Section for PDI Check Screen
 */
@Composable
fun TirePressureSection(
    frontLeftPressure: String,
    onFrontLeftChange: (String) -> Unit,
    frontLeftError: Boolean,
    frontRightPressure: String,
    onFrontRightChange: (String) -> Unit,
    frontRightError: Boolean,
    rearLeftPressure: String,
    onRearLeftChange: (String) -> Unit,
    rearLeftError: Boolean,
    rearRightPressure: String,
    onRearRightChange: (String) -> Unit,
    rearRightError: Boolean,
    tirePressureImageUris: List<Uri>,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDeleteImage: (Int) -> Unit,
    showHelpModal: Boolean,
    onShowHelpModalChange: (Boolean) -> Unit,
    strings: StringResources,
    isLoading: Boolean = false
) {
    Section(
        title = strings.tirePressure,
        showHelpModal = showHelpModal,
        onShowHelpModalChange = onShowHelpModalChange
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = frontLeftPressure,
                    onValueChange = onFrontLeftChange,
                    label = { Text("frontLeftTire", color = if (frontLeftError) MaterialTheme.colorScheme.error else Color.White) },
                    isError = frontLeftError,
                    supportingText = {
                        if (frontLeftPressure.isBlank()) {
                            Text(
                                text = strings.neededField ?: "Campo obrigatório",
                                color = MaterialTheme.colorScheme.error
                            )
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
                )
                OutlinedTextField(
                    value = frontRightPressure,
                    onValueChange = onFrontRightChange,
                    label = { Text("frontRightTire", color = if (frontRightError) MaterialTheme.colorScheme.error else Color.White) },
                    isError = frontRightError,
                    supportingText = {
                        if (frontRightPressure.isBlank()) {
                            Text(
                                text = strings.neededField ?: "Campo obrigatório",
                                color = MaterialTheme.colorScheme.error
                            )
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
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = rearLeftPressure,
                    onValueChange = onRearLeftChange,
                    label = { Text("rearLeftTire", color = if (rearLeftError) MaterialTheme.colorScheme.error else Color.White) },
                    isError = rearLeftError,
                    supportingText = {
                        if (rearLeftPressure.isBlank()) {
                            Text(
                                text = strings.neededField ?: "Campo obrigatório",
                                color = MaterialTheme.colorScheme.error
                            )
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
                )
                OutlinedTextField(
                    value = rearRightPressure,
                    onValueChange = onRearRightChange,
                    label = { Text("rearRightTire", color = if (rearRightError) MaterialTheme.colorScheme.error else Color.White) },
                    isError = rearRightError,
                    supportingText = {
                        if (rearRightPressure.isBlank()) {
                            Text(
                                text = strings.neededField ?: "Campo obrigatório",
                                color = MaterialTheme.colorScheme.error
                            )
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
                )
            }
        }
        ImageUploadField(
            title = strings.tirePressurePhoto,
            imageUris = tirePressureImageUris,
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick,
            onDeleteImage = onDeleteImage,
            strings = strings,
            isLoading = isLoading
        )
    }
}

/**
 * Battery Section for PDI Check Screen
 * Displays the battery voltage field and battery images
 */
@Composable
fun BatterySection(
    batteryVoltage: String,
    onBatteryVoltageChange: (String) -> Unit,
    batteryVoltageError: Boolean,
    battery12VImageUris: List<Uri>,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDeleteImage: (Int) -> Unit,
    showHelpModal: Boolean,
    onShowHelpModalChange: (Boolean) -> Unit,
    strings: StringResources,
    isLoading: Boolean = false
) {
    Section(
        title = strings.batteryVoltage,
        showHelpModal = showHelpModal,
        onShowHelpModalChange = onShowHelpModalChange
    ) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = batteryVoltage,
                onValueChange = onBatteryVoltageChange,
                label = { Text(text = strings.batteryVoltage, color = Color.White) },
                isError = batteryVoltageError,
                supportingText = {
                    if (batteryVoltage.isBlank()) {
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
            imageUris = battery12VImageUris,
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick,
            onDeleteImage = onDeleteImage,
            strings = strings,
            isLoading = isLoading
        )
    }
}

@Composable
fun AdditionalInfoSection(
    additionalInfo: String,
    onAdditionalInfoChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = additionalInfo,
        onValueChange = onAdditionalInfoChange,
        label = { Text(text = LocalStringResources.current.additionalInfo , color = Color.White) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(100.dp),
        maxLines = 5,
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
}
