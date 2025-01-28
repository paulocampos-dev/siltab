package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.models.Car

@Composable
fun VehicleInfoCard(
    selectedCar: Car?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = selectedCar?.name ?: "Unknown Car",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = selectedCar?.type ?: "Unknown Type",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Image(
                painter = selectedCar?.image?.let { painterResource(it) }
                    ?: painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Car Image",
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun TirePressureSection(
    frontLeftPressure: String,
    frontRightPressure: String,
    rearLeftPressure: String,
    rearRightPressure: String,
    onFrontLeftChange: (String) -> Unit,
    onFrontRightChange: (String) -> Unit,
    onRearLeftChange: (String) -> Unit,
    onRearRightChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Pressão dos Pneus",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = frontLeftPressure,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        onFrontLeftChange(newValue)
                    }
                },
                label = { Text("DE") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            OutlinedTextField(
                value = frontRightPressure,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        onFrontRightChange(newValue)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                label = { Text("DD") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = rearLeftPressure,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        onRearLeftChange(newValue)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                label = { Text("TE") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            OutlinedTextField(
                value = rearRightPressure,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        onRearRightChange(newValue)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                label = { Text("TD") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun HybridCarSection(
    batteryVoltage: String,
    voltageImageUri: Uri?,
    onBatteryVoltageChange: (String) -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = batteryVoltage,
            onValueChange = onBatteryVoltageChange,
            label = { Text("Tensão da bateria 12V") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        ImageUploadField(
            title = "Foto da Tensão",
            imageUri = voltageImageUri,
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick
        )
    }
}

@Composable
fun ElectricCarSection(
    isCarStarted: Boolean,
    carStartedImageUri: Uri?,
    onCarStartedChange: (Boolean) -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCarStarted,
                onCheckedChange = onCarStartedChange
            )
            Text(
                text = "O carro foi ligado por 5 minutos?",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        ImageUploadField(
            title = "Foto do Carro Ligado",
            imageUri = carStartedImageUri,
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick
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
        label = { Text("Há alguma informação adicional?") },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(100.dp),
        maxLines = 5
    )
}