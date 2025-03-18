package com.prototype.silver_tab.ui.dialogs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.models.ImageDTO
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.language.LocalStringResources
import com.prototype.silver_tab.utils.getCarImageResource
import com.prototype.silver_tab.viewmodels.InspectionDetailsViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.net.toUri
import com.prototype.silver_tab.BuildConfig
import com.prototype.silver_tab.utils.ImageUtils
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError
import java.io.ByteArrayOutputStream

@Composable
fun InspectionDetailsDialog(
    inspectionInfo: InspectionInfo,
    onDismiss: () -> Unit,
    onMarkAsSold: (InspectionInfo) -> Unit,
    onReportWrongInfo: (InspectionInfo) -> Unit,
    onNewPdi: (InspectionInfo) -> Unit,
    viewModel: InspectionDetailsViewModel = hiltViewModel()
) {
    val strings = LocalStringResources.current
    val context = LocalContext.current

    // UI states
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val images by viewModel.pdiImages.collectAsState()

    // In your InspectionDetailsDialog:
    val allImages by viewModel.pdiImages.collectAsState()

    // Load image data when dialog opens
    LaunchedEffect(inspectionInfo.pdiId) {
        // Directly call the new function that handles loading everything
        viewModel.loadInspectionDetails(inspectionInfo)
    }

    LaunchedEffect(key1 = inspectionInfo.pdiId) {
        // This runs once when the dialog opens
        if (inspectionInfo.pdiId != null) {
            viewModel.loadPdiImages(inspectionInfo.pdiId)
        }
    }

    // Format date helper function
    fun formatDate(dateStr: String?): String {
        if (dateStr == null) return "N/A"

        try {
            // Try to parse the input date
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateStr)

            // Format to the requested format
            return if (date != null) {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)
            } else {
                dateStr
            }
        } catch (e: Exception) {
            return dateStr
        }
    }

    // Confirmation dialog states
    var showWrongInfoConfirmation by remember { mutableStateOf(false) }
    var showMarkAsSoldConfirmation by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                // Header
                Text(
                    text = strings.informationAboutLastPdi,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Main content - scrollable
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (isLoading) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth().fillMaxHeight()
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (error != null) {
                        Text(
                            text = error ?: "Unknown error",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        // Scrollable content
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Car basic info
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Car image
                                Image(
                                    painter = painterResource(id = getCarImageResource(inspectionInfo.name)),
                                    contentDescription = inspectionInfo.name,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(end = 16.dp),
                                    contentScale = ContentScale.Fit
                                )

                                // Car details
                                Column {
                                    Text(
                                        text = inspectionInfo.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )

                                    inspectionInfo.type?.let {
                                        Text(
                                            text = it,
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Text(
//                                        text = "${strings.lastUpdate}: ${formatDate(inspectionInfo.date)}",
                                        text = "Last Update: ${formatDate(inspectionInfo.date)}",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                            // VIN section
                            SectionTitle(title = strings.vin)
                            Text(
                                text = inspectionInfo.vin ?: "N/A",
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // VIN images
                            ImageGallery(
                                images = allImages.filter { it.imageTypeName  == "vin" },
                                emptyMessage = strings.noImageFound
                            )

                            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                            // SOC section
                            SectionTitle(title = strings.socPercentage)
                            Text(
                                text = "${inspectionInfo.soc?.toString() ?: "N/A"} %",
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // SOC images
                            ImageGallery(
                                images = allImages.filter { it.imageTypeName  == "soc" },
                                emptyMessage = strings.noImageFound
                            )

                            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                            // Battery section (if applicable)
                            if (inspectionInfo.battery12v != null) {
                                SectionTitle(title = strings.battery12v)
                                Text(
                                    text = "${inspectionInfo.battery12v} V",
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // Battery images
                                ImageGallery(
                                    images = allImages.filter { it.imageTypeName  == "battery12V" },
                                    emptyMessage = strings.noImageFound
                                )

                                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                            }

                            // Tire pressure section
                            SectionTitle(title = strings.tirePressure)

                            // Car diagram
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.car_draw),
                                    contentDescription = "Car diagram",
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .height(100.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            // Tire pressure readings in a layout similar to CheckScreen
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
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .size(60.dp)
                                            .background(Color(0xFFE0E0E0), RoundedCornerShape(30.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${inspectionInfo.frontLeftTire ?: "N/A"}\nPSI",
                                            textAlign = TextAlign.Center,
                                            color = Color.Black,
                                            fontSize = 14.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }

                                // Front right
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = strings.frontRight,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .size(60.dp)
                                            .background(Color(0xFFE0E0E0), RoundedCornerShape(30.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${inspectionInfo.frontRightTire ?: "N/A"}\nPSI",
                                            textAlign = TextAlign.Center,
                                            color = Color.Black,
                                            fontSize = 14.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
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
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .size(60.dp)
                                            .background(Color(0xFFE0E0E0), RoundedCornerShape(30.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${inspectionInfo.rearLeftTire ?: "N/A"}\nPSI",
                                            textAlign = TextAlign.Center,
                                            color = Color.Black,
                                            fontSize = 14.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }

                                // Rear right
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = strings.rearRight,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .size(60.dp)
                                            .background(Color(0xFFE0E0E0), RoundedCornerShape(30.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${inspectionInfo.rearRightTire ?: "N/A"}\nPSI",
                                            textAlign = TextAlign.Center,
                                            color = Color.Black,
                                            fontSize = 14.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }

                            // Tire images
                            ImageGallery(
                                images = allImages.filter { it.imageTypeName  == "tire" },
                                emptyMessage = strings.noImageFound
                            )

                            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                            // Comments section (if available)
                            if (!inspectionInfo.comments.isNullOrEmpty()) {
                                SectionTitle(title = strings.comments)
                                Text(
                                    text = inspectionInfo.comments,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                            }

                            // Extra images section
                            SectionTitle(title = "Additional Photos")
                            ImageGallery(
                                images = allImages.filter { it.imageTypeName  == "extraImages" },
                                emptyMessage = strings.noImageFound
                            )

                            // Add some space at the bottom
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                // Action buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    // Two rows of buttons in a 2x2 grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Report wrong info button
                        Button(
                            onClick = { showWrongInfoConfirmation = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Report Issue", fontSize = 14.sp)
                        }

                        // Mark as sold button
                        Button(
                            onClick = { showMarkAsSoldConfirmation = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                        ) {
                            Text("Mark As Sold", fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Do new PDI button
                        Button(
                            onClick = { onNewPdi(inspectionInfo) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                        ) {
                            Text("New PDI", fontSize = 14.sp)
                        }

                        // Close button
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("Close", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

    // Wrong info confirmation dialog
    if (showWrongInfoConfirmation) {
        AlertDialog(
            onDismissRequest = { showWrongInfoConfirmation = false },
            title = { Text("Report Wrong Information") },
            text = {
                Column {
                    Text("What information is incorrect?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("This will be reported to the system administrator.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Please provide details in your report.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReportWrongInfo(inspectionInfo)
                        showWrongInfoConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Report Issue")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showWrongInfoConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Mark as sold confirmation dialog
    if (showMarkAsSoldConfirmation) {
        AlertDialog(
            onDismissRequest = { showMarkAsSoldConfirmation = false },
            title = { Text("Mark Vehicle as Sold") },
            text = { Text("Do you want to mark this vehicle as sold? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onMarkAsSold(inspectionInfo)
                        showMarkAsSoldConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("Yes, Mark as Sold")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showMarkAsSoldConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color.DarkGray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun ImageGallery(
    images: List<ImageDTO>,
    emptyMessage: String
) {
    if (images.isEmpty()) {
        Text(
            text = emptyMessage,
            fontSize = 14.sp,
            color = Color.Gray,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(vertical = 8.dp)
        ) {
            items(images) { image ->
                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .height(150.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Show loading indicator initially
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Gray,
                            strokeWidth = 2.dp
                        )

                        // Handle base64 encoded image data
                        if (!image.imageData.isNullOrEmpty()) {
                            // Rotate the image data before displaying
                            val rotatedImageData = rotateBase64Image(image.imageData)

                            val bitmapState = remember(rotatedImageData) {
                                try {
                                    val bitmap = ImageUtils.decodeBase64ToBitmap(rotatedImageData)
                                    if (bitmap != null) {
                                        BitmapResult.Success(bitmap)
                                    } else {
                                        BitmapResult.Error("Failed to decode image")
                                    }
                                } catch (e: Exception) {
                                    BitmapResult.Error(e.message ?: "Error decoding image")
                                }
                            }

                            when (bitmapState) {
                                is BitmapResult.Success -> {
                                    Image(
                                        bitmap = bitmapState.bitmap.asImageBitmap(),
                                        contentDescription = "Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                is BitmapResult.Error -> {
                                    Text(
                                        text = bitmapState.errorMessage,
                                        color = Color.Red,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        } else if (!image.filePath.isNullOrEmpty()) {
                            // Process URL outside composable
                            val fullUrl = remember(image.filePath) {
                                val baseUrl = BuildConfig.BASE_URL // Use your API base URL
                                "$baseUrl/${image.filePath}"
                            }

                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(fullUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = R.drawable.pid_car)
                            )
                        } else {
                            // Fallback for when no image data is available
                            Text(
                                text = "No image data",
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Add this sealed class to handle the bitmap loading result
sealed class BitmapResult {
    data class Success(val bitmap: Bitmap) : BitmapResult()
    data class Error(val errorMessage: String) : BitmapResult()
}


private fun rotateBase64Image(base64ImageData: String): String {
    try {
        // Decode the base64 string to a bitmap
        val imageBytes = Base64.decode(base64ImageData, Base64.DEFAULT)
        var bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        if (bitmap != null) {

            // Rotate the bitmap -90 degrees
            val matrix = Matrix()
            matrix.postRotate(90f)

            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height,
                matrix, true
            )

            // Convert back to base64
            val outputStream = ByteArrayOutputStream()
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val rotatedImageBytes = outputStream.toByteArray()

            // Recycle bitmaps
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }
            rotatedBitmap.recycle()

            return Base64.encodeToString(rotatedImageBytes, Base64.DEFAULT)
        }
    } catch (e: Exception) {
        logTimberError("InspectionDetailsDialog", "Error rotating image: ${e.message}")
    }

    // Return original if rotation fails
    return base64ImageData
}


@Composable
private fun FullScreenImageViewer(
    imageUri: String?,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Full Screen Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}