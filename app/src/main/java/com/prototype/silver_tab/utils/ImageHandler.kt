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
import kotlin.math.max
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.IOException

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

    fun rotateBase64Image(base64ImageData: String): String {
        logTimber("ImageHandler", "Rotating base64 image")

        try {
            // Decode the base64 string to a bitmap
            val imageBytes = Base64.decode(base64ImageData, Base64.DEFAULT)
            var bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            if (bitmap != null) {
                logTimber("ImageHandler", "Original dimensions: ${bitmap.width}x${bitmap.height}")

                // Rotate the bitmap -90 degrees
                val matrix = Matrix()
                matrix.postRotate(-90f)

                val rotatedBitmap = Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.width, bitmap.height,
                    matrix, true
                )

                logTimber("ImageHandler", "Rotated dimensions: ${rotatedBitmap.width}x${rotatedBitmap.height}")

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
            } else {
                logTimberError("ImageHandler", "Failed to decode base64 image")
            }
        } catch (e: Exception) {
            logTimberError("ImageHandler", "Error rotating base64 image: ${e.message}")
        }

        // Return original if rotation fails
        return base64ImageData
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

        var bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, loadOptions)
        } ?: throw IllegalStateException("Failed to load bitmap from URI")

        logTimber("ImageHandler", "Bitmap loaded, about to fix orientation")

        // Fix the orientation based on EXIF data
        bitmap = fixOrientation(uri, bitmap)

        // Further resize the bitmap if it's still too large
        val maxDimension = 1024 // Maximum dimension for the image
        if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
            val scale = maxDimension.toFloat() / max(bitmap.width, bitmap.height)
            val newWidth = (bitmap.width * scale).toInt()
            val newHeight = (bitmap.height * scale).toInt()

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            if (resizedBitmap != bitmap) {
                bitmap.recycle() // Free the original bitmap
                bitmap = resizedBitmap
            }
        }

        return@withContext bitmap
    }

    private fun fixOrientation(uri: Uri, bitmap: Bitmap): Bitmap {
        logTimber("ImageHandler", "Starting image rotation. Original dimensions: ${bitmap.width}x${bitmap.height}")

        try {
            // Apply a fixed -90 degree rotation (counterclockwise)
            val rotationAngle = -90f
            logTimber("ImageHandler", "Applying rotation angle: $rotationAngle degrees")

            val matrix = Matrix()
            matrix.postRotate(rotationAngle)

            // Create a new rotated bitmap
            val startTime = System.currentTimeMillis()
            val rotated = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height,
                matrix, true
            )
            val endTime = System.currentTimeMillis()

            // Check if rotation created a new bitmap
            val isSameBitmap = rotated == bitmap
            logTimber("ImageHandler", "Rotation complete in ${endTime - startTime}ms. New bitmap created: ${!isSameBitmap}")
            logTimber("ImageHandler", "Rotated dimensions: ${rotated.width}x${rotated.height}")

            // If a new bitmap was created, recycle the old one
            if (!isSameBitmap) {
                logTimber("ImageHandler", "Recycling original bitmap")
                bitmap.recycle()
                return rotated
            } else {
                logTimber("ImageHandler", "Warning: Rotation didn't create a new bitmap object")
            }
        } catch (e: Exception) {
            logTimberError("ImageHandler", "Error rotating image: ${e.message}")
            e.printStackTrace()
        }

        logTimber("ImageHandler", "Returning bitmap without rotation")
        return bitmap
    }


    private fun calculateSampleSize(width: Int, height: Int): Int {
        val maxDimension = 1024 // Reduced from your original MAX_IMAGE_SIZE
        val maxDimensionInImage = maxOf(width, height)
        var sampleSize = 1

        while (maxDimensionInImage / sampleSize > maxDimension) {
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
            imageTypeName = type,
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