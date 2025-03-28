package com.prototype.silver_tab.data.repository

import android.content.Context
import android.net.Uri
import com.prototype.silver_tab.data.models.ImageDTO
import com.prototype.silver_tab.data.routes.ImageRoutes
import com.prototype.silver_tab.utils.getFileFromUri
import com.prototype.silver_tab.utils.getFileName
import com.prototype.silver_tab.utils.getMimeType
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val imageRoutes: ImageRoutes
) {
    private val tag = "ImageRepository"

    /**
     * Fetch all images for a specific PDI
     */
    suspend fun getAllPdiImages(pdiId: Int): List<ImageDTO> {
        return withContext(Dispatchers.IO) {
            try {
                logTimber(tag, "Fetching images for PDI: $pdiId")
                val response = imageRoutes.getPdiImages(pdiId)

                if (response.isSuccessful) {
                    val images = response.body() ?: emptyList()
                    logTimber(tag, "Successfully fetched ${images.size} images for PDI: $pdiId")
                    images
                } else {
                    logTimberError(tag, "Error fetching PDI images: ${response.code()} - ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                logTimberError(tag, "Exception fetching PDI images: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun uploadPdiImage(
        pdiId: Int,
        imageType: String,
        imageUri: Uri,
        context: Context
    ): Result<ImageDTO> {
        return withContext(Dispatchers.IO) {
            try {
                logTimber(tag, "Preparing to upload image for PDI: $pdiId, type: $imageType")

                // Get file from URI
                val file = getFileFromUri(context, imageUri)
                    ?: return@withContext Result.failure(Exception("Failed to read image file"))

                // Determine file name and mime type
                val fileName = getFileName(context, imageUri) ?: "image.jpg"
                val mimeType = getMimeType(context, imageUri) ?: "image/jpeg"

                // Create multipart request
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile)
                val typeBody = imageType.toRequestBody("text/plain".toMediaTypeOrNull())

                // Make API call
                val response = imageRoutes.uploadPdiImage(pdiId, typeBody, filePart)

                if (response.isSuccessful) {
                    val uploadedImage = response.body()
                    if (uploadedImage != null) {
                        logTimber(tag, "Successfully uploaded image for PDI: $pdiId, image ID: ${uploadedImage.imageId}")
                        Result.success(uploadedImage)
                    } else {
                        logTimberError(tag, "Upload successful but received null response")
                        Result.failure(Exception("Received null response after successful upload"))
                    }
                } else {
                    val errorMsg = "Failed to upload image: ${response.code()} - ${response.message()}"
                    logTimberError(tag, errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                logTimberError(tag, "Exception uploading image: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun deletePdiImage(imageId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                logTimber(tag, "Making API call to delete image with ID: $imageId")

                // Log the complete URL that will be called
                logTimber(tag, "DELETE API endpoint: image/pdi/$imageId")

                val response = imageRoutes.deletePdiImage(imageId)

                // Log raw response details
                logTimber(tag, "Delete API response code: ${response.code()}")
                logTimber(tag, "Delete API response message: ${response.message()}")

                // Try to get error body if available
                if (!response.isSuccessful) {
                    try {
                        val errorBody = response.errorBody()?.string()
                        logTimberError(tag, "Delete API error body: $errorBody")
                    } catch (e: Exception) {
                        // Ignore if we can't read the error body
                    }
                }

                if (response.isSuccessful) {
                    logTimber(tag, "Successfully deleted image with ID: $imageId")
                    Result.success(Unit)
                } else {
                    val errorMsg = "Failed to delete image: ${response.code()} - ${response.message()}"
                    logTimberError(tag, errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                logTimberError(tag, "Exception deleting PDI image: ${e.message}")
                e.printStackTrace() // Print full stack trace for debugging
                Result.failure(e)
            }
        }
    }
}