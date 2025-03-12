package com.prototype.silver_tab.ui.components.checkscreen

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.ui.components.ImageUploadField
import com.prototype.silver_tab.utils.StringResources

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
    strings: StringResources
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
                    label = { Text("DE", color = if (frontLeftError) MaterialTheme.colorScheme.error else Color.White) },
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
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
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
                    label = { Text("DD", color = if (frontRightError) MaterialTheme.colorScheme.error else Color.White) },
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
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = rearLeftPressure,
                    onValueChange = onRearLeftChange,
                    label = { Text("TE", color = if (rearLeftError) MaterialTheme.colorScheme.error else Color.White) },
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
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
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
                    label = { Text("TD", color = if (rearRightError) MaterialTheme.colorScheme.error else Color.White) },
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
            strings = strings
        )
    }
}
