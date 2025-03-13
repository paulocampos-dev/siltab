package com.prototype.silver_tab.data.repository

import android.content.Context
import android.net.Uri
import com.prototype.silver_tab.data.models.ImageDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

/**
 * Interface for image repository operations.
 * This defines the contract for image-related operations.
 */
interface ImageRepository {
    /**
     * Fetches all images for a PDI
     * @param pdiId The ID of the PDI to get images for
     * @return List of ImageDTO objects or null if error
     */
    suspend fun getAllPdiImages(pdiId: Int): List<ImageDTO>?

    /**
     * Converts an ImageDTO to a URI that can be used in the app
     * @param imageDTO The ImageDTO containing the image data or path
     * @return A Uri that can be used to display the image, or null if conversion fails
     */
    suspend fun convertImageDtoToUri(imageDTO: ImageDTO): Uri?

    /**
     * Upload multiple images for a PDI
     * @param context Android context
     * @param uris List of image URIs to upload
     * @param pdiId The ID of the PDI to upload images for
     * @param imageType The type of image (e.g., "vin", "soc", etc.)
     */
    suspend fun uploadImages(
        context: Context,
        uris: List<Uri>,
        pdiId: Int,
        imageType: String
    )

    /**
     * Delete multiple PDI images
     * @param imageIds Set of image IDs to delete
     * @return Map of image IDs to deletion success status
     */
    suspend fun deletePdiImages(imageIds: Set<Int>): Map<Int, Boolean>

    /**
     * Delete a PDI image
     * @param imageId The ID of the image to delete
     * @return Success or failure result
     */
    suspend fun deletePdiImage(imageId: Int): Result<Boolean>

    /**
     * Helper method to upload a single image
     */
    suspend fun uploadPdiImage(
        pdiId: Int,
        imageType: RequestBody,
        file: MultipartBody.Part
    ): Response<ImageDTO>

}