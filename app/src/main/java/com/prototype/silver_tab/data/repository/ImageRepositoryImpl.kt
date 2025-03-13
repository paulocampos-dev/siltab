package com.prototype.silver_tab.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import com.prototype.silver_tab.BuildConfig
import com.prototype.silver_tab.data.api_connection.routes.ImageRoutes
import com.prototype.silver_tab.data.models.ImageDTO
import com.prototype.silver_tab.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ImageRepository interface for handling all image-related operations.
 */
@Singleton
class ImageRepositoryImpl @Inject constructor(
    private val imageRoutes: ImageRoutes
) : ImageRepository {

    /**
     * Fetches all images for a PDI
     * @param pdiId The ID of the PDI to get images for
     * @return List of ImageDTO objects or null if error
     */
    override suspend fun getAllPdiImages(pdiId: Int): List<ImageDTO>? {
        return withContext(Dispatchers.IO) {
            try {
                Timber.d("Fetching images for PDI ID: $pdiId")
                val response = imageRoutes.getPdiImages(pdiId)
                if (response.isSuccessful) {
                    val images = response.body()
                    Timber.d("Successfully fetched ${images?.size ?: 0} images for PDI ID: $pdiId")
                    images
                } else {
                    val errorBody = response.errorBody()?.string()
                    Timber.e("Error getting PDI images: $errorBody")
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Error getting PDI images for PDI ID: $pdiId")
                null
            }
        }
    }

    /**
     * Converts an ImageDTO to a URI that can be used in the app
     * @param imageDTO The ImageDTO containing the image data or path
     * @return A Uri that can be used to display the image, or null if conversion fails
     */
    override suspend fun convertImageDtoToUri(imageDTO: ImageDTO): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val context = com.prototype.silver_tab.SilverTabApplication.instance

                if (imageDTO.imageData != null) {
                    // If we have base64 image data, decode it with subsampling for memory efficiency
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }

                    // First decode with inJustDecodeBounds=true to check dimensions
                    val imageBytes = Base64.decode(imageDTO.imageData, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

                    // Calculate inSampleSize to reduce memory usage
                    var inSampleSize = 1
                    if (options.outHeight > 800 || options.outWidth > 800) {
                        val halfHeight = options.outHeight / 2
                        val halfWidth = options.outWidth / 2

                        while ((halfHeight / inSampleSize) >= 800 &&
                            (halfWidth / inSampleSize) >= 800) {
                            inSampleSize *= 2
                        }
                    }

                    // Decode bitmap with inSampleSize set
                    options.inSampleSize = inSampleSize
                    options.inJustDecodeBounds = false

                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

                    if (bitmap != null) {
                        // Create a temporary file with a unique name
                        val file = File.createTempFile(
                            "img_${System.currentTimeMillis()}_",
                            ".jpg",
                            context.cacheDir
                        )

                        FileOutputStream(file).use { fos ->
                            // Use a lower quality to save memory
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos)
                        }

                        // Recycle bitmap to free memory immediately
                        bitmap.recycle()

                        // Convert to content Uri using FileProvider
                        return@withContext FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                    }
                } else if (imageDTO.filePath != null) {
                    // For remote images, use a more efficient approach
                    val fullUrl = "${BuildConfig.BASE_URL}/${imageDTO.filePath}"

                    // Create a placeholder file first
                    val file = File.createTempFile(
                        "remote_img_${System.currentTimeMillis()}_",
                        ".jpg",
                        context.cacheDir
                    )

                    try {
                        // Use OkHttp for efficient downloading
                        val client = OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(15, TimeUnit.SECONDS)
                            .build()

                        val request = Request.Builder().url(fullUrl).build()
                        val response = client.newCall(request).execute()

                        if (!response.isSuccessful) {
                            Timber.e("Failed to download image: ${response.code}")
                            return@withContext null
                        }

                        // Stream the response directly to the file
                        FileOutputStream(file).use { outputStream ->
                            response.body?.byteStream()?.use { inputStream ->
                                inputStream.copyTo(outputStream, 4096)
                            }
                        }

                        return@withContext FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                    } catch (e: Exception) {
                        Timber.e(e, "Error downloading image from $fullUrl")
                        return@withContext null
                    }
                }

                return@withContext null
            } catch (e: Exception) {
                Timber.e(e, "Error converting image")
                return@withContext null
            }
        }
    }

    /**
     * Upload multiple images for a PDI
     * @param context Android context
     * @param uris List of image URIs to upload
     * @param pdiId The ID of the PDI to upload images for
     * @param imageType The type of image (e.g., "vin", "soc", etc.)
     */
    override suspend fun uploadImages(
        context: Context,
        uris: List<Uri>,
        pdiId: Int,
        imageType: String
    ) {
        // Early return if there are no images to upload
        if(uris.isEmpty()) {
            return
        }

        // Get the auth repository instance
        val authRepository = com.prototype.silver_tab.SilverTabApplication.authRepository

        for (uri in uris) {
            try {
                // Check for access token using the repository
                val accessToken = authRepository.getAccessToken()
                if (accessToken.isNullOrEmpty()) {
                    withContext(Dispatchers.Main) {
                        Timber.e("Auth error: Access token missing.")
                    }
                    continue // Skip this image if no token is available
                }

                val tempFile = FileUtils.getFileFromUri(context, uri) ?: continue
                if (!tempFile.exists()) continue

                val fileName = FileUtils.getFileName(context, uri)
                val mimeType = FileUtils.getMimeType(context, uri) ?: ""

                val requestFile = tempFile.asRequestBody(mimeType.toMediaType())
                val multipartBody = MultipartBody.Part.createFormData("file", fileName, requestFile)

                val response = uploadPdiImage(
                    pdiId = pdiId,
                    imageType = imageType.toRequestBody("text/plain".toMediaTypeOrNull()),
                    file = multipartBody
                )

                if (response.isSuccessful) {
                    Timber.d("Image '$fileName' uploaded successfully!")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Timber.e("Failed to upload image '$fileName' for PDI $pdiId: ${response.code()} $errorBody")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error uploading image for PDI $pdiId")
            }
        }
    }

    /**
     * Helper method to upload a single image
     */
    override suspend fun uploadPdiImage(
        pdiId: Int,
        imageType: RequestBody,
        file: MultipartBody.Part
    ): Response<ImageDTO> {
        return withContext(Dispatchers.IO) {
            try {
                Timber.d("Uploading image for PDI ID: $pdiId, type: ${imageType.toString()}")
                imageRoutes.uploadPdiImage(pdi = pdiId, pdiImageType = imageType, file = file)
            } catch (e: Exception) {
                Timber.e(e, "Error uploading image for PDI: $pdiId")
                throw e
            }
        }
    }

    /**
     * Delete multiple PDI images
     * @param imageIds Set of image IDs to delete
     * @return Map of image IDs to deletion success status
     */
    override suspend fun deletePdiImages(imageIds: Set<Int>): Map<Int, Boolean> {
        val results = mutableMapOf<Int, Boolean>()

        for (imageId in imageIds) {
            try {
                Timber.d("Deleting image ID: $imageId")
                val result = deletePdiImage(imageId)
                results[imageId] = result.isSuccess
                if (result.isSuccess) {
                    Timber.d("Successfully deleted image ID: $imageId")
                } else {
                    Timber.e("Failed to delete image ID: $imageId")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error deleting PDI image ID $imageId")
                results[imageId] = false
            }
        }

        return results
    }

    /**
     * Delete a PDI image
     * @param imageId The ID of the image to delete
     * @return Success or failure result
     */
    override suspend fun deletePdiImage(imageId: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = imageRoutes.deletePdiImage(imageId)
                if (response.isSuccessful) {
                    Timber.d("Successfully deleted image ID: $imageId")
                    Result.success(true)
                } else {
                    val errorMessage = "Failed to delete image: ${response.code()} ${response.message()}"
                    Timber.e(errorMessage)
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Timber.e(e, "Error deleting PDI image")
                Result.failure(e)
            }
        }
    }
}