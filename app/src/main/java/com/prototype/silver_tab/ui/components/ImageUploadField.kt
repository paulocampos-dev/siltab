//package com.prototype.silver_tab.ui.components
//
//import android.net.Uri
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.vectorResource
//import androidx.compose.ui.unit.dp
//import coil3.compose.AsyncImage
//import coil3.request.ImageRequest
//import coil3.request.crossfade
//import com.prototype.silver_tab.R
//import com.prototype.silver_tab.utils.StringResources
//
//@Composable
//fun ImageUploadField(
//    title: String,
//    imageUris: List<Uri>,
//    onCameraClick: () -> Unit,
//    onGalleryClick: () -> Unit,
//    onDeleteImage: (Int) -> Unit,
//    modifier: Modifier = Modifier,
//    strings: StringResources,
//    maxImages: Int = 4,
//    isLoading: Boolean = false
//) {
//    var showSourceDialog by remember { mutableStateOf(false) }
//
//    // Source selection dialog
//    if (showSourceDialog) {
//        AlertDialog(
//            onDismissRequest = { showSourceDialog = false },
//            title = { Text(text = strings.selectImageSource ?: "Select Image Source") },
//            text = { Text(text = strings.selectImageSourceDescription ?: "Choose how you want to add an image") },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        onCameraClick()
//                        showSourceDialog = false
//                    }
//                ) {
//                    Icon(
//                        imageVector = ImageVector.vectorResource(R.drawable.baseline_camera),
//                        contentDescription = null,
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(strings.camera)
//                }
//            },
//            dismissButton = {
//                Button(
//                    onClick = {
//                        onGalleryClick()
//                        showSourceDialog = false
//                    }
//                ) {
//                    Icon(
//                        imageVector = ImageVector.vectorResource(R.drawable.baseline_photo_camera_back),
//                        contentDescription = null,
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(strings.gallery)
//                }
//            }
//        )
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//    ) {
//        Text(
//            text = title,
//            style = MaterialTheme.typography.titleMedium,
//            modifier = Modifier.padding(bottom = 8.dp),
//            color = Color.White
//        )
//
//        // Upload buttons - only show if there are no images yet or if there's still room for more
//        if (imageUris.size < maxImages) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 8.dp),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                OutlinedButton(
//                    onClick = onCameraClick,
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        contentColor = Color.White
//                    )
//                ) {
//                    Row(
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = ImageVector.vectorResource(R.drawable.baseline_camera),
//                            contentDescription = null,
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(strings.camera)
//                    }
//                }
//
//                OutlinedButton(
//                    onClick = onGalleryClick,
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        contentColor = Color.White
//                    )
//                ) {
//                    Row(
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = ImageVector.vectorResource(R.drawable.baseline_photo_camera_back),
//                            contentDescription = null,
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(strings.gallery)
//                    }
//                }
//            }
//        }
//
//        if (isLoading) {
//            // Show loading state
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    CircularProgressIndicator(color = Color.White)
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        text = strings.loadingImages,
//                        color = Color.White
//                    )
//                }
//            }
//        } else if (imageUris.isEmpty()) {
//            // Empty state
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(100.dp)
//                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
//                    .padding(16.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = strings.noImageSelected,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.Gray
//                )
//            }
//        } else {
//            // Use LazyVerticalGrid for better memory management with image lists
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                contentPadding = PaddingValues(4.dp),
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(if (imageUris.isEmpty() && imageUris.size < maxImages) 0.dp else 200.dp)
//            ) {
//                // Existing images - use Coil's AsyncImage for efficient loading
//                items(imageUris.size) { index ->
//                    Box(
//                        modifier = Modifier
//                            .aspectRatio(1f)
//                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
//                    ) {
//                        // Use Coil's AsyncImage for memory-efficient image loading
//                        AsyncImage(
//                            model = ImageRequest.Builder(LocalContext.current)
//                                .data(imageUris[index])
//                                .crossfade(true)
//                                .size(300, 300) // Limit size to save memory
//                                .build(),
//                            contentDescription = null,
//                            modifier = Modifier.fillMaxSize(),
//                            contentScale = ContentScale.Crop
//                        )
//
//                        // Delete button
//                        IconButton(
//                            onClick = { onDeleteImage(index) },
//                            modifier = Modifier
//                                .align(Alignment.TopEnd)
//                                .padding(4.dp)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Delete,
//                                contentDescription = null,
//                                tint = Color.Red
//                            )
//                        }
//                    }
//                }
//
//                // Empty slots with + icons that are clickable
//                if (imageUris.size < maxImages) {
//                    val emptySlots = maxImages - imageUris.size
//                    items(emptySlots) {
//                        Box(
//                            modifier = Modifier
//                                .aspectRatio(1f)
//                                .border(
//                                    width = 1.dp,
//                                    color = Color.Gray,
//                                    shape = RoundedCornerShape(8.dp)
//                                )
//                                .clickable {
//                                    showSourceDialog = true
//                                },
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Add,
//                                contentDescription = "Add image",
//                                tint = Color.Gray,
//                                modifier = Modifier.size(40.dp)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}