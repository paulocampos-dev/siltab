package com.prototype.silver_tab.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.prototype.silver_tab.data.models.ImageDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.min

/**
 * Utility class for handling images in the app
 */
class ImageHandler(private val context: Context) {

    companion object {
        private const val MAX_IMAGE_SIZE = 800 // Maximum dimension for images
        private const val JPEG_QUALITY = 85 // JPEG quality (0-100)
        private const val TEMP_IMAGE_PREFIX = "pdi_image_"
    }

    /**
     * Create a temporary file for saving camera images
     */
    fun createImageFile(): File {
        val storageDir = context.cacheDir
        return File.createTempFile(
            TEMP_IMAGE_PREFIX + UUID.randomUUID().toString(),
            ".jpg",
            storageDir
        )
    }

    /**
     * Process and optimize an image from URI
     * Returns a File pointing to the processed image
     */
    suspend fun processImageFromUri(uri: Uri): File = withContext(Dispatchers.IO) {
        val bitmap = loadAndResizeBitmap(uri)
        val file = createImageFile()

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
        }

        bitmap.recycle()
        return@withContext file
    }

    /**
     * Load and resize a bitmap from URI
     */
    private suspend fun loadAndResizeBitmap(uri: Uri): Bitmap = withContext(Dispatchers.IO) {
        // Load bitmap options to check dimensions without loading the full bitmap
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }

        // Calculate sample size for downsampling
        val sampleSize = calculateSampleSize(options.outWidth, options.outHeight)

        // Load downsampled bitmap
        val loadOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }

        val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, loadOptions)
        } ?: throw IllegalStateException("Failed to load bitmap from URI")

        return@withContext bitmap
    }

    /**
     * Calculate sample size for downsampling
     */
    private fun calculateSampleSize(width: Int, height: Int): Int {
        val maxDimension = maxOf(width, height)
        var sampleSize = 1

        while (maxDimension / sampleSize > MAX_IMAGE_SIZE) {
            sampleSize *= 2
        }

        return sampleSize
    }

    /**
     * Convert a bitmap to base64 string
     */
    suspend fun bitmapToBase64(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
        val byteArray = outputStream.toByteArray()
        return@withContext Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /**
     * Create an ImageDTO from a processed image file
     */
    suspend fun createImageDTO(file: File, type: String): ImageDTO = withContext(Dispatchers.IO) {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val base64 = bitmapToBase64(bitmap)
        bitmap.recycle()

        return@withContext ImageDTO(
            imageId = null,
            pdiId = null,
            pdiImageType = type,
            imageData = base64,
            fileName = file.name,
            filePath = file.path
        )
    }

    /**
     * Decode a base64 string to bitmap
     */
    fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            logTimberError("ImageHandler", "Error decoding base64 to bitmap: ${e.message}")
            null
        }
    }
}