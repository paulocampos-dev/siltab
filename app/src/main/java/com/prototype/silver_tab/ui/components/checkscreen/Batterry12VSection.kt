package com.prototype.silver_tab.ui.components.checkscreen

import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
    strings: StringResources
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
            strings = strings
        )
    }
}
