package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.api_connection.RetrofitClient
import com.prototype.silver_tab.data.models.ImageDTO
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.fakeInspectionInfoLists
import com.prototype.silver_tab.data.repository.ImageRepository
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.utils.ImageUtils
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.StringResources
import com.prototype.silver_tab.utils.formatRelativeDate
import com.prototype.silver_tab.viewmodels.InspectionInfoViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun InspectionInfoCard(
    inspectionInfo: InspectionInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStringResources.current

    // Function to get translated vehicle type
    fun getTranslatedType(type: String?): String {
        return when (type?.lowercase()) {
            "híbrido", "hybrid", "hibrido" -> strings.vehicleTypeHybrid
            "elétrico", "electric", "eletrico" -> strings.vehicleTypeElectric
            else -> type ?: ""
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var img = R.drawable.pid_car
            when (inspectionInfo.name) {
                "BYD YUAN PLUS" -> img = R.drawable.byd_yuan_plus
                "BYD TAN" -> img =  R.drawable.byd_tan
                "BYD YUAN PRO" -> img = R.drawable.byd_yuan_pro
                "BYD SEAL" -> img = R.drawable.pid_car
                "BYD HAN" -> img = R.drawable.byd_han
                "BYD DOLPHIN PLUS" -> img = R.drawable.byd_dolphin_plus
                "BYD DOLPHIN" -> img = R.drawable.byd_dolphin
                "BYD DOLPHIN MINI" -> img = R.drawable.byd_dolphin_mini
                "BYD SONG PRO DM-i" -> img = R.drawable.byd_song_pro
                "SONG PLUS PREMIUM DM-i" -> img = R.drawable.byd_song_premium
                "BYD SONG PLUS DM-i" -> img = R.drawable.byd_song_premium
                "BYD KING DM-i" -> img = R.drawable.byd_king
                "BYD SHARK" -> img = R.drawable.byd_shark
            }
            Image(
                painter = inspectionInfo.image?.let { painterResource(it) } ?: painterResource(img),
                contentDescription = inspectionInfo.chassi,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                inspectionInfo.chassi?.let {
                    Text(it, color = Color.White, fontWeight = FontWeight.Bold)
                } ?: inspectionInfo.name?.let {Text(it, color = Color.White, fontWeight = FontWeight.Bold)}

                // Use the translated type here
                if (inspectionInfo.type != null) {
                    Text(
                        text = getTranslatedType(inspectionInfo.type),
                        color = Color.Gray
                    )
                }

                if (inspectionInfo.date != null) {
                    Text(text = formatRelativeDate(inspectionInfo.date), color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun InspectionInfoList(inspectionInfoList: List<InspectionInfo>, onCarClicked: (InspectionInfo) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items = inspectionInfoList) { car ->
            InspectionInfoCard(inspectionInfo = car, onClick = { onCarClicked(car) })
        }
    }
}

@Composable
fun InspectionInfoModalDialog(
    inspectionInfo: InspectionInfo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onChangeHistoricPDI: (InspectionInfo) -> Unit,
    onNewPdi: (InspectionInfo) -> Unit,
    viewModel: InspectionInfoViewModel = hiltViewModel(),
    strings: StringResources = LocalStringResources.current
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showVinCorrectionDialog by remember { mutableStateOf(false) }

    var pdiImages by remember { mutableStateOf<List<ImageDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(inspectionInfo.pdiId) {
        try {
            inspectionInfo.pdiId?.let { pdiId ->
                pdiImages = viewModel.getPdiImages(pdiId)
            }
        } catch (e: Exception) {
            pdiImages = emptyList()
        } finally {
            isLoading = false
        }
    }

    // Function to format date in a human-readable way
    fun formatDate(dateStr: String?): String {
        if (dateStr == null) return "N/A"

        return try {
            // Try parsing an ISO date format with time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(dateStr) ?: return dateStr

            // Format date as "dd/MM/yyyy HH:mm"
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            // If the format doesn't match, try a simpler date format (without time)
            try {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = simpleDateFormat.parse(dateStr) ?: return dateStr

                // Format date as "dd/MM/yyyy"
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                outputFormat.format(date)
            } catch (e: Exception) {
                // Return original if unable to parse
                dateStr
            }
        }
    }

    AlertDialog(
        modifier = Modifier.fillMaxHeight(),
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = { Text(text = strings.informationAboutLastPdi, fontWeight = FontWeight.Bold, color = Color.Black) },
        text = {
            Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Column {
                    Text("Nome: ${inspectionInfo.name}")
                    Text("Última atualização: ${formatDate(inspectionInfo.date)}")
                    Spacer(modifier = Modifier.height(16.dp))

                    // Car image section
                    var img = R.drawable.pid_car
                    when (inspectionInfo.name) {
                        "BYD YUAN PLUS" -> img = R.drawable.byd_yuan_plus
                        "BYD TAN" -> img =  R.drawable.byd_tan
                        "BYD YUAN PRO" -> img = R.drawable.byd_yuan_pro
                        "BYD SEAL" -> img = R.drawable.pid_car
                        "BYD HAN" -> img = R.drawable.byd_han
                        "BYD DOLPHIN PLUS" -> img = R.drawable.byd_dolphin_plus
                        "BYD DOLPHIN" -> img = R.drawable.byd_dolphin
                        "BYD DOLPHIN MINI" -> img = R.drawable.byd_dolphin_mini
                        "BYD SONG PRO DM-i" -> img = R.drawable.byd_song_pro
                        "SONG PLUS PREMIUM DM-i" -> img = R.drawable.byd_song_premium
                        "BYD SONG PLUS DM-i" -> img = R.drawable.byd_song_premium
                        "BYD KING DM-i" -> img = R.drawable.byd_king
                        "BYD SHARK" -> img = R.drawable.byd_shark
                    }
                    Image(
                        painter = inspectionInfo.image?.let { painterResource(it) }
                            ?: painterResource(img),
                        contentDescription = inspectionInfo.chassi,
                        modifier = Modifier
                            .aspectRatio(16 / 9f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    //histórico do chassi
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.Vin, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))

                        inspectionInfo.chassi?.let { Text(it, color = Color.Black) }

                        Spacer(modifier = Modifier.height(8.dp))


                        // Horizontal scrollable images for Chassis section
                        if (isLoading) {
                            Text(strings.loadingImages)
                        } else {
                            val chassiImages = pdiImages.filter {
                                it.imageTypeName?.lowercase(Locale.ROOT)?.contains("vin") == true ||
                                        it.description?.lowercase(Locale.ROOT)?.contains("vin") == true
                            }

                            if (chassiImages.isNotEmpty()) {
                                Box(modifier = Modifier.height(220.dp)) {
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        items(chassiImages) { imageDTO ->
                                            Column(
                                                modifier = Modifier
                                                    .width(300.dp)
                                                    .padding(horizontal = 8.dp)
                                            ) {
                                                DisplayImage(imageDTO)
//                                                Text(
//                                                    text = "Chassi: ${imageDTO.fileName ?: "Unknown"}",
//                                                    maxLines = 1,
//                                                    overflow = TextOverflow.Ellipsis
//                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(strings.noImageFound)
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SOC %", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = inspectionInfo.soc.toString(), color = Color.Black)

                        Spacer(modifier = Modifier.height(8.dp))

                        // Horizontal scrollable images for SOC section
                        if (isLoading) {
                            Text(strings.loadingImages)
                        } else {
                            val socImages = pdiImages.filter {
                                it.imageTypeName?.lowercase(Locale.getDefault())?.contains("soc") == true
                            }

                            if (socImages.isNotEmpty()) {
                                Box(modifier = Modifier.height(220.dp)) {
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        items(socImages) { imageDTO ->
                                            Column(
                                                modifier = Modifier
                                                    .width(300.dp)
                                                    .padding(horizontal = 8.dp)
                                            ) {
                                                DisplayImage(imageDTO)
//                                                Text(
//                                                    text = "SOC: ${imageDTO.fileName ?: "Unknown"}",
//                                                    maxLines = 1,
//                                                    overflow = TextOverflow.Ellipsis
//                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(strings.noImageFound)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.tirePressure, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("DE")
                                Text(text = "${inspectionInfo.DE?.toString() ?: "XX"} PSI", color = Color.Black)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("DD")
                                Text(text = "${inspectionInfo.DD?.toString() ?: "XX"} PSI", color = Color.Black)
                            }
                        }

                        Image(
                            painter = painterResource(R.drawable.car_draw),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TE")
                                Text(text = "${inspectionInfo.TE?.toString() ?: "XX"} PSI", color = Color.Black)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TD")
                                Text(text = "${inspectionInfo.TD?.toString() ?: "XX"} PSI", color = Color.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Horizontal scrollable images for tire pressure section
                        if (isLoading) {
                            Text(strings.loadingImages)
                        } else {
                            val tireImages = pdiImages.filter {
                                it.imageTypeName?.lowercase(Locale.ROOT)?.contains("tire") == true
                            }

                            if (tireImages.isNotEmpty()) {
                                Box(modifier = Modifier.height(220.dp)) {
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        items(tireImages) { imageDTO ->
                                            Column(
                                                modifier = Modifier
                                                    .width(300.dp)
                                                    .padding(horizontal = 8.dp)
                                            ) {
                                                DisplayImage(imageDTO)
//                                                Text(
//                                                    text = "Tire: ${imageDTO.fileName ?: "Unknown"}",
//                                                    maxLines = 1,
//                                                    overflow = TextOverflow.Ellipsis
//                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(strings.noImageFound)
                            }
                        }
                    }

                    // Other images section
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Extra Images", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (isLoading) {
                            Text(strings.loadingImages)
                        } else {
                            val otherImages = pdiImages.filter {
                                it.imageTypeName?.lowercase(Locale.ROOT)?.contains("extraImages") == true
                            }

                            if (otherImages.isNotEmpty()) {
                                Box(modifier = Modifier.height(220.dp)) {
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        items(otherImages) { imageDTO ->
                                            Column(
                                                modifier = Modifier
                                                    .width(300.dp)
                                                    .padding(horizontal = 8.dp)
                                            ) {
                                                DisplayImage(imageDTO)
                                                Text(
                                                    text = "${imageDTO.fileName ?: "Unknown"}",
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(strings.noImageFound)
                            }
                        }
                    }

                }
            }
        },
        confirmButton = {
            // Replace the existing button with a column containing all three buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button 1: Report Wrong Information
                Button(
                    onClick = { showConfirmationDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Wrong Information", color = Color.White)
                }

                // Button 2: Do New PDI
                Button(
                    onClick = { onNewPdi(inspectionInfo) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("Do New PDI", color = Color.White)
                }

                // Button 3: Close
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        },
    )
    if (showConfirmationDialog) {
        ConfirmationDialog(
            inspecInfo = inspectionInfo,
            onDismiss = { showConfirmationDialog = false },
            onChangeHistoricPDI =  { onChangeHistoricPDI(inspectionInfo)},
            onNewPdi = {onNewPdi(inspectionInfo) },
            onWrongVinSelected = {
                showConfirmationDialog = false
                showVinCorrectionDialog = true
            },
            strings = strings
        )
    }

    // VIN correction dialog
    if (showVinCorrectionDialog) {
        VinCorrectionDialog(
            show = true,
            onDismiss = { showVinCorrectionDialog = false },
            originalVin = inspectionInfo.chassi ?: "",
            onSubmitNewVin = { newVin ->
                // TODO: Here you'll call your API endpoint to update the VIN
                // This will depend on your API implementation
//                submitVinCorrection(inspectionInfo, newVin)
                showVinCorrectionDialog = false
                onDismiss() // Close the main dialog after submission
            },
            strings = strings
        )
    }
}

// Helper composable to display an image from ImageDTO
@Composable
private fun DisplayImage(imageDTO: ImageDTO) {
    if (!imageDTO.imageData.isNullOrEmpty()) {
        // Decode Base64 image data.
        val bitmap = ImageUtils.decodeBase64ToBitmap(imageDTO.imageData!!)
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Decoded Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Text("Erro ao decodificar a imagem")
        }
    } else {
        // Fallback: load image via URL.
        val fullUrl = "${RetrofitClient.BASE_URL}/${imageDTO.filePath}"
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(fullUrl)
                .crossfade(true)
                .build(),
            contentDescription = imageDTO.fileName,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
@Preview(showBackground = true)
fun PreviewCarComponents() {
    val selectedInspectionInfo = remember { mutableStateOf<InspectionInfo?>(null) }

    // Main UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor) // Replace with your theme's background color
    ) {
        Text(
            text = "Car List",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            color = Color.White
        )

        InspectionInfoList(inspectionInfoList = fakeInspectionInfoLists) { car ->
            selectedInspectionInfo.value = car
        }
    }

    // Dialog UI
    selectedInspectionInfo.value?.let { car ->
        InspectionInfoModalDialog(
            inspectionInfo = car,
            onDismiss = { selectedInspectionInfo.value = null },
            onChangeHistoricPDI = {},
            onNewPdi = {}
        )
    }
}