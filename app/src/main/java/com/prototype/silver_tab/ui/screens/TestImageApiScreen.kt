package com.prototype.silver_tab.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.api.AuthManager
import com.prototype.silver_tab.data.api.RetrofitClient
import com.prototype.silver_tab.data.api.ImageAPI
import com.prototype.silver_tab.data.models.ImageDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

@Composable
fun TestImageApiScreen() {
    val context = LocalContext.current

    // Ensure a valid token is set before making any API calls.
    if (AuthManager.getAccessToken().isNullOrEmpty()) {
        AuthManager.setTokens("dummyValidToken", "dummyRefreshToken")
    }

    var uploadResult by remember { mutableStateOf("") }
    var getResult by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var images by remember { mutableStateOf<List<ImageDTO>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Launcher to pick an image from the gallery.
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uploadResult = "Image selected: ${uri?.toString()}"
    }

    // Use the centralized ImageAPI from RetrofitClient.
    val imageApi: ImageAPI = RetrofitClient.imageapi

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- Upload Section ---
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Pick Image")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            coroutineScope.launch {
                if (selectedImageUri == null) {
                    withContext(Dispatchers.Main) { uploadResult = "No image selected" }
                    return@launch
                }
                try {
                    if (AuthManager.getAccessToken().isNullOrEmpty()) {
                        withContext(Dispatchers.Main) {
                            uploadResult = "Error: No authentication token available. Please login first."
                        }
                        return@launch
                    }
                    val tempFile = withContext(Dispatchers.IO) {
                        getFileFromUri(context, selectedImageUri!!)
                    }
                    if (tempFile == null || !tempFile.exists()) {
                        withContext(Dispatchers.Main) { uploadResult = "Error: Unable to read the selected file." }
                        return@launch
                    }
                    val fileName = tempFile.name
                    val mimeType = getMimeType(context, selectedImageUri!!) ?: "image/jpeg"

                    val requestFile: RequestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
                    val multipartBody: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "file",
                        fileName,
                        requestFile
                    )
                    val imageTypeBody: RequestBody = "CHASSI".toRequestBody("text/plain".toMediaTypeOrNull())
                    val response = imageApi.uploadDealerImage(
                        pdi = 1,
                        pdiImageType = imageTypeBody,
                        file = multipartBody
                    )
                    withContext(Dispatchers.Main) {
                        uploadResult = if (response.isSuccessful) {
                            "Upload successful! Image ID: ${response.body()?.imageId}"
                        } else {
                            val errorMessage = try {
                                response.errorBody()?.string()
                            } catch (e: Exception) {
                                "Error reading error body"
                            }
                            "Upload failed: ${response.code()} $errorMessage"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        uploadResult = "Upload error: ${e.message}\n${e.stackTraceToString()}"
                    }
                }
            }
        }) {
            Text("Upload Image")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = uploadResult)

        Spacer(modifier = Modifier.height(16.dp))

        // --- GET PDI Images Section ---
        Button(onClick = {
            coroutineScope.launch {
                try {
                    val pdiId = 1
                    val response = imageApi.getPdiImages(pdiId = pdiId, pdiImageType = null)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            images = response.body() ?: emptyList()
                            getResult = if (images.isEmpty()) "No images found." else "Found ${images.size} images."
                        } else {
                            getResult = "Failed: ${response.code()} ${response.errorBody()?.string()}"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { getResult = "Error: ${e.localizedMessage}" }
                }
            }
        }) {
            Text("Fetch PDI Images")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = getResult)

        Spacer(modifier = Modifier.height(16.dp))

        // --- Display Images ---
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(images) { imageDTO ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    if (imageDTO.imageData?.isNotEmpty() == true) {
                        // Decode Base64 image
                        val bitmap = imageDTO.imageData?.let { decodeBase64ToBitmap(it) }
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Decoded Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        } ?: Text("Error decoding image")
                    } else {
                        // Load image via URL
                        val fullUrl = "${RetrofitClient.BASE_URL}/${imageDTO.filePath}"
                        Log.d("ImageURL", "Loading Image from: $fullUrl")

                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(fullUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = imageDTO.fileName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(text = "File: ${imageDTO.fileName ?: "Unknown"}")
                }
            }
        }
    }
}

private suspend fun getFileFromUri(context: Context, uri: Uri): File? = withContext(Dispatchers.IO) {
    try {
        val fileName = getFileName(context, uri) ?: "temp_image.jpg"
        val tempFile = File(context.cacheDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun decodeBase64ToBitmap(base64String: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getMimeType(context: Context, uri: Uri): String? {
    return context.contentResolver.getType(uri)
}

fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    result = cursor.getString(nameIndex)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path?.substringAfterLast('/')
    }
    if (result != null && !result!!.contains(".")) {
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val extension = android.webkit.MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType) ?: "jpg"
        result += ".$extension"
    }
    return result
}


