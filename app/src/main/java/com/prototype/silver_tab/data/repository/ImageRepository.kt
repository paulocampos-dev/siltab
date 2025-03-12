package com.prototype.silver_tab.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.data.api_connection.routes.ImageRoutes
import com.prototype.silver_tab.data.api_connection.RetrofitClient
import com.prototype.silver_tab.data.models.ImageDTO
import com.prototype.silver_tab.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import timber.log.Timber

object ImageRepository {
    private val imageRoutes: ImageRoutes = RetrofitClient.imageRoutes

    suspend fun uploadImage(pdiId: Int, imageType: RequestBody, file: MultipartBody.Part): Response<ImageDTO> {
        return withContext(Dispatchers.IO) {
            try {
                imageRoutes.uploadPdiImage(pdi = pdiId, pdiImageType = imageType, file = file)
            } catch (e: Exception) {
                Timber.e(e, "Erro ao fazer upload da imagem para PDI: $pdiId")
                //saveLogToFile("Erro uploadImage: ${e.message}")
                throw e
            }
        }
    }

    suspend fun getAllPdiImages(pdiId: Int): List<ImageDTO>? {
        return withContext(Dispatchers.IO) {
                try {
                    val response = imageRoutes.getPdiImages(pdiId = pdiId, pdiImageType = null)
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Timber.e("Erro ao obter imagens do PDI $pdiId: $errorBody")
                        //saveLogToFile("Erro getAllPdiImages: $errorBody")
                        null
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Erro inesperado ao buscar imagens do PDI: $pdiId")
                    //saveLogToFile("Erro inesperado getAllPdiImages: ${e.message}")
                    null
                }
        }
    }
    suspend fun getPdiImagesByTypeName(pdiId: Int, pdiImageTypeName: String): List<ImageDTO>? {
        return withContext(Dispatchers.IO) {
            val response = imageRoutes.getPdiImages(pdiId = pdiId, pdiImageType = pdiImageTypeName)
            if (response.isSuccessful) response.body() else null
        }
    }

    suspend fun uploadImages(
        context: Context,
        uris: List<Uri>,
        pdiId: Int,
        imageType: String
    ) {
        // Early return if there are no images to upload
        if(uris.isEmpty()){
            return
        }

        // Get the auth repository instance
        val authRepository = SilverTabApplication.authRepository

        for (uri in uris){
            try{
                // Check for access token using the repository
                val accessToken = authRepository.getAccessToken()
                if (accessToken.isNullOrEmpty()){
                    withContext(Dispatchers.Main){

//                        var uploadResult = "Error: No authentication token available."
                        Timber.e("Erro de autenticação: Token de acesso ausente.")
                    }
                    continue // Skip this image if no token is available
                }

                val tempFile = FileUtils.getFileFromUri(context, uri) ?: continue
                if (!tempFile.exists()) continue

                val fileName = FileUtils.getFileName(context, uri)
                val mimeType = FileUtils.getMimeType(context, uri) ?: ""

                val requestFile = tempFile.asRequestBody(mimeType.toMediaType())
                val multipartBody = MultipartBody.Part.createFormData("file", fileName, requestFile)

                val response = uploadImage(
                    pdiId = pdiId,
                    imageType = imageType.toRequestBody("text/plain".toMediaTypeOrNull()),
                    file = multipartBody
                )

                if (response.isSuccessful){
                    Log.d("UPLOAD_IMAGE", "Image '$fileName' uploaded successfully!")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Timber.e("Falha no upload da imagem '$fileName' para PDI $pdiId: ${response.code()} $errorBody")

                }
            } catch (e: Exception) {
                Timber.e(e, "Erro ao enviar imagem para PDI $pdiId")
            }
        }
    }

    // Method to delete a single PDI image
    suspend fun deletePdiImage(imageId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = imageRoutes.deletePdiImage(imageId)
                val success = response.isSuccessful
                if (!success) {
                    val errorBody = response.errorBody()?.string()
                    Timber.e("Failed to delete PDI image ID $imageId: ${response.code()} $errorBody")
                }
                success
            } catch (e: Exception) {
                Timber.e(e, "Error deleting PDI image ID $imageId")
                false
            }
        }
    }

    // Method to delete multiple PDI images
    suspend fun deletePdiImages(imageIds: Set<Int>): Map<Int, Boolean> {
        val results = mutableMapOf<Int, Boolean>()

        for (imageId in imageIds) {
            results[imageId] = deletePdiImage(imageId)
        }

        return results
    }
}


/**
 * Utility class to handle image processing and tracking for PDIs.
 * This separates image-specific operations from the main repository.
 */
class ImageProcessingHelper {

    companion object {
        /**
         * Process a single image for upload
         * @param context Application context
         * @param uri Image URI to upload
         * @return MultipartBody.Part ready for upload or null if processing failed
         */
        suspend fun processImageForUpload(
            context: Context,
            uri: Uri
        ): MultipartBody.Part? = withContext(Dispatchers.IO) {
            try {
                val tempFile = FileUtils.getFileFromUri(context, uri) ?: return@withContext null
                if (!tempFile.exists()) return@withContext null

                val fileName = FileUtils.getFileName(context, uri) ?: "unknown.jpg"
                val mimeType = FileUtils.getMimeType(context, uri) ?: "image/jpeg"

                val requestFile = tempFile.asRequestBody(mimeType.toMediaType())
                return@withContext MultipartBody.Part.createFormData("file", fileName, requestFile)
            } catch (e: Exception) {
                Timber.e(e, "Error processing image for upload")
                return@withContext null
            }
        }

        /**
         * Create a type request body for image upload
         * @param imageType Type of the image (e.g., "vin", "soc", etc.)
         * @return RequestBody for the image type
         */
        fun createImageTypeRequestBody(imageType: String) =
            imageType.toRequestBody("text/plain".toMediaTypeOrNull())

        /**
         * Get access token for image operations
         * @return Token string or null if not available
         */
        suspend fun getAccessToken(): String? {
            return SilverTabApplication.authRepository.getAccessToken()
        }
    }
}

/**
 * Data class to track image upload status
 */
data class ImageUploadStatus(
    val uri: Uri,
    val success: Boolean,
    val imageId: Int? = null,
    val errorMessage: String? = null
)

/**
 * Data class to track image deletion status
 */
data class ImageDeletionStatus(
    val imageId: Int,
    val success: Boolean,
    val errorMessage: String? = null
)

/**
 * Manages tracking changes to images between the original state and current state
 */
class ImageChangeTracker {
    private val newImages = mutableMapOf<String, MutableList<Uri>>()
    private val deletedImageIds = mutableSetOf<Int>()
    private val imageIdMap = mutableMapOf<Uri, Int>()

    /**
     * Add a new image to track
     */
    fun addNewImage(type: String, uri: Uri) {
        val imagesOfType = newImages.getOrPut(type) { mutableListOf() }
        imagesOfType.add(uri)
    }

    /**
     * Mark an image as deleted
     */
    fun markImageDeleted(imageId: Int) {
        deletedImageIds.add(imageId)
    }

    /**
     * Track an existing image's ID
     */
    fun trackImageId(uri: Uri, imageId: Int) {
        imageIdMap[uri] = imageId
    }

    /**
     * Get image ID for a URI if available
     */
    fun getImageId(uri: Uri): Int? = imageIdMap[uri]

    /**
     * Get all newly added images of a specific type
     */
    fun getNewImagesOfType(type: String): List<Uri> = newImages[type] ?: emptyList()

    /**
     * Get all image IDs that were marked for deletion
     */
    fun getDeletedImageIds(): Set<Int> = deletedImageIds

    /**
     * Check if an image exists in the tracked set
     */
    fun hasExistingImage(uri: Uri): Boolean = imageIdMap.containsKey(uri)
}
