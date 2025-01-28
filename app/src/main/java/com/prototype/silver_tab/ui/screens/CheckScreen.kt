package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.prototype.silver_tab.R

@Composable
fun CheckScreen(
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }
    var chassisNumber by remember { mutableStateOf("") }
    var batteryVoltage by remember { mutableStateOf("") }
    var socPercentage by remember { mutableStateOf("") }
    var isCarStarted by remember { mutableStateOf(false) }
    var additionalInfo by remember { mutableStateOf("") }

    // Tire pressure values
    var frontLeftPressure by remember { mutableStateOf("") }
    var frontRightPressure by remember { mutableStateOf("") }
    var rearLeftPressure by remember { mutableStateOf("") }
    var rearRightPressure by remember { mutableStateOf("") }

    // Image URI states
    var chassisImageUri by remember { mutableStateOf<Uri?>(null) }
    var batteryImageUri by remember { mutableStateOf<Uri?>(null) }
    var voltageImageUri by remember { mutableStateOf<Uri?>(null) }
    var tirePressureImageUri by remember { mutableStateOf<Uri?>(null) }
    var carStartedImageUri by remember { mutableStateOf<Uri?>(null) }
    var additionalImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Handle the selected image
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        // Handle the captured image
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Vehicle info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "DOLPHIN MINI",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Elétrico",
                        color = Color.Blue,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Image(
                    //painter = painterResource(id = R.drawable.dolphin_mini),
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "BYD Dolphin Mini",
                    modifier = Modifier.size(80.dp)
                )
            }
        }

        // Form fields
        OutlinedTextField(
            value = chassisNumber,
            onValueChange = { chassisNumber = it },
            label = { Text("Chassi do veículo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        ImageUploadField(
            title = "Foto do Chassi",
            imageUri = chassisImageUri,
            onCameraClick = { /* Launch camera */ },
            onGalleryClick = { imagePicker.launch("image/*") }
        )

        OutlinedTextField(
            value = socPercentage,
            onValueChange = { socPercentage = it },
            label = { Text("Percentual do SOC medido") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        ImageUploadField(
            title = "Foto da Bateria",
            imageUri = batteryImageUri,
            onCameraClick = { /* Launch camera */ },
            onGalleryClick = { imagePicker.launch("image/*") }
        )

        OutlinedTextField(
            value = batteryVoltage,
            onValueChange = { batteryVoltage = it },
            label = { Text("Tensão da bateria 12V") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Tire pressure section
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
                onValueChange = { frontLeftPressure = it },
                label = { Text("DE") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            OutlinedTextField(
                value = frontRightPressure,
                onValueChange = { frontRightPressure = it },
                label = { Text("DD") },
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
                onValueChange = { rearLeftPressure = it },
                label = { Text("TE") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            OutlinedTextField(
                value = rearRightPressure,
                onValueChange = { rearRightPressure = it },
                label = { Text("TD") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }

        ImageUploadField(
            title = "Foto da Pressão dos Pneus",
            imageUri = tirePressureImageUri,
            onCameraClick = { /* Launch camera */ },
            onGalleryClick = { imagePicker.launch("image/*") }
        )

        // Car started verification
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCarStarted,
                onCheckedChange = { isCarStarted = it }
            )
            Text(
                text = "O carro foi ligado por 5 minutos?",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        ImageUploadField(
            title = "Foto do Carro Ligado",
            imageUri = carStartedImageUri,
            onCameraClick = { /* Launch camera */ },
            onGalleryClick = { imagePicker.launch("image/*") }
        )

        // Additional information
        OutlinedTextField(
            value = additionalInfo,
            onValueChange = { additionalInfo = it },
            label = { Text("Há alguma informação adicional?") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(100.dp),
            maxLines = 5
        )

        Button(
            onClick = { showFinishDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Finalizar")
        }
    }

    // Dialogs
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancelar?") },
            text = { Text("Tem certeza que deseja cancelar? Todos os dados preenchidos até agora serão perdidos.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Sim, cancelar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Não, voltar")
                }
            }
        )
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Concluir?") },
            text = { Text("O processo PID será encerrado e você não poderá alterar as informações depois. Tem certeza que quer concluir?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFinishDialog = false
                        onFinish()
                    }
                ) {
                    Text("Concluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ImageUploadField(
    title: String,
    imageUri: Uri?,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(title)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onCameraClick) {
                Icon(
                    //painter = painterResource(id = R.drawable.ic_camera),
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Take photo"
                )
            }
            IconButton(onClick = onGalleryClick) {
                Icon(
                    //painter = painterResource(id = R.drawable.ic_gallery),
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Choose from gallery"
                )
            }
        }
        if (imageUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Não há imagens adicionadas ainda")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckScreenPreview() {
    MaterialTheme {
        CheckScreen(
            onNavigateBack = {},
            onFinish = {}
        )

    }
}