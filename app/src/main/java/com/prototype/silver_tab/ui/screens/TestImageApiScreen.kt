package com.prototype.silver_tab.ui.screens

//import android.net.Uri
//import android.util.Log
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import coil3.compose.AsyncImage
//import coil3.request.ImageRequest
//import coil3.request.crossfade
//import com.prototype.silver_tab.data.models.ImageDTO
//import com.prototype.silver_tab.data.repository.ImageRepository
//import com.prototype.silver_tab.utils.FileUtils
//import com.prototype.silver_tab.utils.ImageUtils
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.asRequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//
//@Composable
//fun TestImageApiScreen() {
//    val context = LocalContext.current
//
//    // Ensure a valid token is set before making any API calls.
//    if (AuthManager.getAccessToken().isNullOrEmpty()) {
//        AuthManager.setTokens("dummyValidToken", "dummyRefreshToken")
//    }
//
//    var uploadResult by remember { mutableStateOf("") }
//    var getResult by remember { mutableStateOf("") }
//    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
//    var images by remember { mutableStateOf<List<ImageDTO>>(emptyList()) }
//    val coroutineScope = rememberCoroutineScope()
//
//    // Launcher to pick an image from the gallery.
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        selectedImageUri = uri
//        uploadResult = "Image selected: ${uri?.toString()}"
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        // --- Upload Section ---
//        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
//            Text("Pick Image")
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//        Button(onClick = {
//            coroutineScope.launch {
//                if (selectedImageUri == null) {
//                    withContext(Dispatchers.Main) { uploadResult = "No image selected" }
//                    return@launch
//                }
//                try {
//                    if (AuthManager.getAccessToken().isNullOrEmpty()) {
//                        withContext(Dispatchers.Main) {
//                            uploadResult = "Error: No authentication token available. Please login first."
//                        }
//                        return@launch
//                    }
//                    val tempFile = FileUtils.getFileFromUri(context, selectedImageUri!!)
//                    if (tempFile == null || !tempFile.exists()) {
//                        withContext(Dispatchers.Main) { uploadResult = "Error: Unable to read the selected file." }
//                        return@launch
//                    }
//                    val fileName = FileUtils.getFileName(context, selectedImageUri!!) ?: "unknown.jpg"
//                    val mimeType = FileUtils.getMimeType(context, selectedImageUri!!) ?: "image/jpeg"
//
//                    val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
//                    val multipartBody = MultipartBody.Part.createFormData("file", fileName, requestFile)
//                    val imageTypeBody = "CHASSI".toRequestBody("text/plain".toMediaTypeOrNull())
//
//                    val response = ImageRepository.uploadImage(
//                        pdiId = 1,
//                        imageType = imageTypeBody,
//                        file = multipartBody
//                    )
//
//                    withContext(Dispatchers.Main) {
//                        uploadResult = if (response.isSuccessful) {
//                            "Upload successful! Image ID: ${response.body()?.imageId}"
//                        } else {
//                            val errorMessage = try {
//                                response.errorBody()?.string()
//                            } catch (e: Exception) {
//                                "Error reading error body"
//                            }
//                            "Upload failed: ${response.code()} $errorMessage"
//                        }
//                    }
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        uploadResult = "Upload error: ${e.message}\n${e.stackTraceToString()}"
//                    }
//                }
//            }
//        }) {
//            Text("Upload Image")
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = uploadResult)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // --- GET PDI Images Section ---
//        Button(onClick = {
//            coroutineScope.launch {
//                try {
//                    val pdiId = 1
//                    val fetchedImages = ImageRepository.getAllPdiImages(pdiId)
//
//                    withContext(Dispatchers.Main) {
//                        images = fetchedImages ?: emptyList()
//                        getResult = if (images.isEmpty()) "No images found." else "Found ${images.size} images."
//                    }
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) { getResult = "Error: ${e.localizedMessage}" }
//                }
//            }
//        }) {
//            Text("Fetch PDI Images")
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = getResult)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // --- Display Images ---
//        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            items(images) { imageDTO ->
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp)
//                ) {
//                    if (!imageDTO.imageData.isNullOrEmpty()) {
//                        // Decode Base64 image
//                        val bitmap = ImageUtils.decodeBase64ToBitmap(imageDTO.imageData!!)
//                        bitmap?.let {
//                            Image(
//                                bitmap = it.asImageBitmap(),
//                                contentDescription = "Decoded Image",
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(200.dp)
//                            )
//                        } ?: Text("Error decoding image")
//                    } else {
//                        // Load image via URL
//                        val fullUrl = "${com.prototype.silver_tab.data.api_connection.RetrofitClient.BASE_URL}/${imageDTO.filePath}"
//                        Log.d("ImageURL", "Loading Image from: $fullUrl")
//
//                        AsyncImage(
//                            model = ImageRequest.Builder(LocalContext.current)
//                                .data(fullUrl)
//                                .crossfade(true)
//                                .build(),
//                            contentDescription = imageDTO.fileName,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(200.dp),
//                            contentScale = ContentScale.Crop
//                        )
//                    }
//                    Text(text = "File: ${imageDTO.fileName ?: "Unknown"}")
//                }
//            }
//        }
//    }
//}
