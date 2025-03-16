//package com.prototype.silver_tab.ui.dialogs
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import coil3.compose.AsyncImage
//import coil3.request.ImageRequest
//import coil3.request.crossfade
//import com.prototype.silver_tab.R
//import com.prototype.silver_tab.data.models.ImageDTO
//import com.prototype.silver_tab.data.models.InspectionInfo
//import com.prototype.silver_tab.ui.components.ConfirmationDialog
//import com.prototype.silver_tab.ui.components.VinCorrectionDialog
//import com.prototype.silver_tab.utils.ImageUtils
//import com.prototype.silver_tab.utils.LocalStringResources
//import com.prototype.silver_tab.utils.StringResources
//import com.prototype.silver_tab.utils.getCarImageResource
//import java.text.SimpleDateFormat
//import java.util.Locale
//
//@Composable
//fun InspectionInfoModalDialog(
//    inspectionInfo: InspectionInfo,
//    onDismiss: () -> Unit,
//    modifier: Modifier = Modifier,
//    onChangeHistoricPDI: (InspectionInfo) -> Unit,
//    onNewPdi: (InspectionInfo) -> Unit,
//    strings: StringResources = LocalStringResources.current,
//    viewModel: InspectionDetailsViewModel = hiltViewModel()
//) {
//    var showConfirmationDialog by remember { mutableStateOf(false) }
//    var showVinCorrectionDialog by remember { mutableStateOf(false) }
//
//    // Use viewModel state for images and loading state
//    val pdiImages by viewModel.pdiImages.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val error by viewModel.error.collectAsState()
//
//    // Load images when dialog is opened
//    LaunchedEffect(inspectionInfo.pdiId) {
//        inspectionInfo.pdiId?.let { pdiId ->
//            viewModel.loadPdiImages(pdiId)
//        }
//    }
//
//    // Function to format date in a human-readable way
//    fun formatDate(dateStr: String?): String {
//        if (dateStr == null) return "N/A"
//
//        return try {
//            // Try parsing an ISO date format with time
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
//            val date = dateFormat.parse(dateStr) ?: return dateStr
//
//            // Format date as "dd/MM/yyyy HH:mm"
//            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
//            outputFormat.format(date)
//        } catch (e: Exception) {
//            // If the format doesn't match, try a simpler date format (without time)
//            try {
//                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                val date = simpleDateFormat.parse(dateStr) ?: return dateStr
//
//                // Format date as "dd/MM/yyyy"
//                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//                outputFormat.format(date)
//            } catch (e: Exception) {
//                // Return original if unable to parse
//                dateStr
//            }
//        }
//    }
//
//    AlertDialog(
//        modifier = Modifier.fillMaxHeight(0.8f),
//        containerColor = Color.White,
//        onDismissRequest = onDismiss,
//        title = { Text(text = strings.informationAboutLastPdi, fontWeight = FontWeight.Bold, color = Color.Black) },
//        text = {
//            Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
//                Column {
//                    // Error message if applicable
////                    error?.let {
////                        Text(
////                            text = it,
////                            color = Color.Red,
////                            fontWeight = FontWeight.Bold,
////                            modifier = Modifier.padding(bottom = 8.dp)
////                        )
////                    }
//
//                    Text("Nome: ${inspectionInfo.name}")
//                    Text("Última atualização: ${formatDate(inspectionInfo.date)}")
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    Image(
//                        painter =painterResource(getCarImageResource(inspectionInfo.name)),
//                        contentDescription = inspectionInfo.vin,
//                        modifier = Modifier
//                            .aspectRatio(16 / 9f)
//                    )
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    // VIN section
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text(strings.Vin, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        inspectionInfo.vin?.let { Text(it, color = Color.Black) }
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        // Horizontal scrollable images for Chassis section
//                        ImageSection(
//                            isLoading = isLoading,
//                            images = viewModel.getImagesOfType("vin"),
//                            emptyMessage = strings.noImageFound,
//                            loadingMessage = strings.loadingImages
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    // SOC section
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text("SOC %", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        Text(
//                            text = inspectionInfo.soc?.toString() ?: "N/A",
//                            color = Color.Black
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        // Horizontal scrollable images for SOC section
//                        ImageSection(
//                            isLoading = isLoading,
//                            images = viewModel.getImagesOfType("soc"),
//                            emptyMessage = strings.noImageFound,
//                            loadingMessage = strings.loadingImages
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    // Tire pressure section
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text(strings.tirePressure, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceEvenly
//                        ) {
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Text("frontLeftTire")
//                                Text(text = "${inspectionInfo.frontLeftTire?.toString() ?: "XX"} PSI", color = Color.Black)
//                            }
//
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Text("frontRightTire")
//                                Text(text = "${inspectionInfo.frontRightTire?.toString() ?: "XX"} PSI", color = Color.Black)
//                            }
//                        }
//
//                        Image(
//                            painter = painterResource(R.drawable.car_draw),
//                            contentDescription = null,
//                            modifier = Modifier.fillMaxWidth(0.5f)
//                        )
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceEvenly
//                        ) {
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Text("rearLeftTire")
//                                Text(text = "${inspectionInfo.rearLeftTire?.toString() ?: "XX"} PSI", color = Color.Black)
//                            }
//
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Text("rearRightTire")
//                                Text(text = "${inspectionInfo.rearRightTire?.toString() ?: "XX"} PSI", color = Color.Black)
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        // Horizontal scrollable images for tire pressure section
//                        ImageSection(
//                            isLoading = isLoading,
//                            images = viewModel.getImagesOfType("tire"),
//                            emptyMessage = strings.noImageFound,
//                            loadingMessage = strings.loadingImages
//                        )
//                    }
//
//                    // Extra images section
//                    Spacer(modifier = Modifier.height(24.dp))
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text("Extra Images", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        ImageSection(
//                            isLoading = isLoading,
//                            images = viewModel.getImagesOfType("extraImages"),
//                            emptyMessage = strings.noImageFound,
//                            loadingMessage = strings.loadingImages,
//                            showFileName = true
//                        )
//                    }
//                }
//            }
//        },
//        confirmButton = {
//            // Column containing all three buttons
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                // Button 1: Report Wrong Information
//                Button(
//                    onClick = { showConfirmationDialog = true },
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//                ) {
//                    Text("Wrong Information", color = Color.White)
//                }
//
//                // Button 2: Do New PDI
//                Button(
//                    onClick = { onNewPdi(inspectionInfo) },
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
//                ) {
//                    Text("Do New PDI", color = Color.White)
//                }
//
//                // Button 3: Close
//                Button(
//                    onClick = onDismiss,
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
//                ) {
//                    Text("Close", color = Color.White)
//                }
//            }
//        },
//    )
//
//    // Confirmation dialog
//    if (showConfirmationDialog) {
//        ConfirmationDialog(
//            inspecInfo = inspectionInfo,
//            onDismiss = { showConfirmationDialog = false },
//            onChangeHistoricPDI = { onChangeHistoricPDI(inspectionInfo) },
//            onNewPdi = { onNewPdi(inspectionInfo) },
//            onWrongVinSelected = {
//                showConfirmationDialog = false
//                showVinCorrectionDialog = true
//            },
//            strings = strings
//        )
//    }
//
//    // VIN correction dialog
//    if (showVinCorrectionDialog) {
//        VinCorrectionDialog(
//            show = true,
//            onDismiss = { showVinCorrectionDialog = false },
//            originalVin = inspectionInfo.vin ?: "",
//            onSubmitNewVin = { newVin ->
//                viewModel.submitVinCorrection(inspectionInfo, newVin)
//                showVinCorrectionDialog = false
//                onDismiss() // Close the main dialog after submission
//            },
//            strings = strings
//        )
//    }
//}
//
//// Reusable component for displaying image sections
//@Composable
//private fun ImageSection(
//    isLoading: Boolean,
//    images: List<ImageDTO>,
//    emptyMessage: String,
//    loadingMessage: String,
//    showFileName: Boolean = false
//) {
//    if (isLoading) {
//        Text(loadingMessage)
//    } else if (images.isEmpty()) {
//        Text(emptyMessage)
//    } else {
//        Box(modifier = Modifier.height(220.dp)) {
//            LazyRow(
//                modifier = Modifier.fillMaxWidth(),
//                contentPadding = PaddingValues(horizontal = 8.dp)
//            ) {
//                items(images) { imageDTO ->
//                    Column(
//                        modifier = Modifier
//                            .width(300.dp)
//                            .padding(horizontal = 8.dp)
//                    ) {
//                        DisplayImage(imageDTO)
//                        if (showFileName) {
//                            Text(
//                                text = imageDTO.fileName ?: "Unknown",
//                                maxLines = 1,
//                                overflow = TextOverflow.Ellipsis
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// Helper composable to display an image from ImageDTO
//@Composable
//private fun DisplayImage(imageDTO: ImageDTO) {
//    if (!imageDTO.imageData.isNullOrEmpty()) {
//        // Decode Base64 image data
//        val bitmap = ImageUtils.decodeBase64ToBitmap(imageDTO.imageData!!)
//        if (bitmap != null) {
//            Image(
//                bitmap = bitmap.asImageBitmap(),
//                contentDescription = "Decoded Image",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(180.dp),
//                contentScale = ContentScale.Crop
//            )
//        } else {
//            Text("Erro ao decodificar a imagem")
//        }
//    } else {
//        // Try to load from URL if available
//        val baseUrl = BuildConfig.BASE_URL
//        imageDTO.filePath?.let { path ->
//            val fullUrl = "$baseUrl/$path"
//            AsyncImage(
//                model = ImageRequest.Builder(LocalContext.current)
//                    .data(fullUrl)
//                    .crossfade(true)
//                    .build(),
//                contentDescription = imageDTO.fileName,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(180.dp),
//                contentScale = ContentScale.Crop
//            )
//        } ?: Text("No image available")
//    }
//}