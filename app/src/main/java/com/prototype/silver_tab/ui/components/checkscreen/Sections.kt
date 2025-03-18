import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.models.ImageDTO
import com.prototype.silver_tab.language.LocalStringResources
import com.prototype.silver_tab.language.StringResources
import com.prototype.silver_tab.ui.components.help.HelpButton
import com.prototype.silver_tab.ui.components.help.HelpModal
import com.prototype.silver_tab.utils.logTimber


@Composable
fun SectionCard(
    title: String,
    content: @Composable () -> Unit,
    helpType: String? = null,
    helpImage: Int? = null,
    strings: StringResources = LocalStringResources.current
) {

    var showHelpModal by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF333333)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // Only show help button if helpType is provided
                if (helpType != null) {
                    HelpButton {
                        showHelpModal = true
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }

        // Show help modal if requested
        if (showHelpModal && helpType != null) {
            HelpModal(
                onDismiss = { showHelpModal = false },
                img = helpImage,
                type = helpType,
                strings = strings
            )
        }
    }
}

@Composable
fun ImageSection(
    title: String,
    images: List<ImageDTO>,
    onAddImage: (Uri) -> Unit,
    onRemoveImage: (Int) -> Unit,
    maxImages: Int
) {
    var showImageSourceDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val strings = LocalStringResources.current
    val scrollState = rememberScrollState()

    // Create mutable state for camera URI
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            cameraImageUri?.let { onAddImage(it) }
        }
    }

    // Add permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, now you can launch camera
            // Create a new temp file for this specific photo
            val newTempFile = context.cacheDir.resolve("temp_image_${System.currentTimeMillis()}.jpg")
            cameraImageUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                newTempFile
            )
            cameraLauncher.launch(cameraImageUri)
        } else {
            // Show error message that camera permission is required
            Toast.makeText(context, "Camera permission is required to take photos", Toast.LENGTH_LONG).show()
        }
    }

    // Image picker launcher (gallery)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onAddImage(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Display images row - now horizontally scrollable
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Existing images - add a key for each image to ensure proper recomposition
            images.forEachIndexed { index, image ->
                key(image.filePath, image.imageData, index) {  // Add key with unique properties
                    ImageThumbnail(
                        image = image,
                        onRemove = { onRemoveImage(index) }
                    )
                }
            }

            // Add image button (if not at max)
            if (images.size < maxImages) {
                Box(
                    modifier = Modifier
                        .size(120.dp)  // Increase size to match new image size
                        .background(Color.DarkGray, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .clickable { showImageSourceDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add image",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Add",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Hint text
        if (images.isEmpty()) {
            Text(
                text = "Tap + to add images (max $maxImages)",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Image source dialog
        if (showImageSourceDialog) {
            AlertDialog(
                onDismissRequest = { showImageSourceDialog = false },
                title = { Text("Add Image") },
                text = { Text("Choose image source") },
                confirmButton = {
                    Button(
                        onClick = {
                            showImageSourceDialog = false
                            galleryLauncher.launch("image/*")
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_gallery),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Gallery")
                    }
                },
                dismissButton = {
                    // In your AlertDialog where you handle image source selection
                    Button(
                        onClick = {
                            showImageSourceDialog = false
                            // Check for camera permission before launching camera
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                                PackageManager.PERMISSION_GRANTED) {
                                // Permission already granted, proceed with camera launch
                                val newTempFile = context.cacheDir.resolve("temp_image_${System.currentTimeMillis()}.jpg")
                                cameraImageUri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    newTempFile
                                )
                                cameraLauncher.launch(cameraImageUri)
                            } else {
                                // Request permission
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_camera),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Camera")
                    }
                }
            )
        }
    }
}


@Composable
fun ImageThumbnail(
    image: ImageDTO,
    onRemove: () -> Unit
) {
    val tag = "ImageThumbnail"
    logTimber(tag, "Started")
    logTimber(tag, "Loading image from URI: ${image.filePath}")
    logTimber(tag, "Loading image with DATA: ${image.imageData}")
    logTimber(tag, "Loading image with TYPE: ${image.pdiImageType}")
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(120.dp)  // Increased image size
            .background(Color.DarkGray, RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
    ) {
        // Try to load image from URI if filePath is available
        if (!image.filePath.isNullOrEmpty()) {
            // Use AsyncImage from Coil for image loading
            val imageUri = Uri.parse(image.filePath)
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
        }
        // Try to load from base64 if imageData is available
        else if (!image.imageData.isNullOrEmpty()) {
            // Handle base64 decoding outside of composable
            val bitmap = remember(image.imageData) {
                try {
                    val imageBytes = Base64.decode(image.imageData, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                } catch (e: Exception) {
                    null
                }
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback if bitmap is null
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Invalid Image",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center)
                )
            }
        } else {
            // Placeholder if no image data
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "No Image",
                tint = Color.White,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Center)
            )
        }

        // Remove button overlay - smaller and in top-right corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp) // Adjust the circle size as needed
                .background(Color.Red, CircleShape)
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.size(16.dp) // Adjust icon size relative to the circle
            )
        }
    }
}