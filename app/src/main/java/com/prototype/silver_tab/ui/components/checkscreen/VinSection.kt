package com.prototype.silver_tab.ui.components.checkscreen

import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.ui.components.ImageUploadField
import com.prototype.silver_tab.utils.StringResources

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
    enabled: Boolean = true
) {
    Section(
        title = strings.chassisNumber,
        showHelpModal = showHelpModal,
        onShowHelpModalChange = onShowHelpModalChange
    ) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
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
            strings = strings
        )
    }
}
