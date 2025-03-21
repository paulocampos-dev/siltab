package com.prototype.silver_tab.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CameraUtils(private val context: Context) {

    // Create a temporary file for storing camera image
    fun createImageFile(): File {
        // Create a unique filename
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_${timeStamp}_"

        // Get app's private storage directory
        val storageDir = context.getExternalFilesDir("images")

        return File.createTempFile(
            fileName,
            ".jpg",
            storageDir
        ).apply {
            Timber.d("Created temporary file: $absolutePath")
        }
    }

    /**
     * Get a content URI for a file using FileProvider
     */
    fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    /**
     * Get a File from a URI
     */
    fun getFileFromUri(uri: Uri): File? {
        return try {
            // Attempt to get the file path from the URI
            val path = uri.path?.let { File(it) }

            if (path?.exists() == true) {
                path
            } else {
                // If the direct path doesn't work, we need to copy the content
                copyUriToFile(uri)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error converting URI to file: $uri")
            null
        }
    }

    /**
     * Copy content from URI to a temporary file
     */
    private fun copyUriToFile(uri: Uri): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val tempFile = File.createTempFile(
                "URI_${timeStamp}_",
                ".jpg",
                context.getExternalFilesDir("images")
            )

            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            tempFile
        } catch (e: Exception) {
            Timber.e(e, "Error copying URI content to file: $uri")
            null
        }
    }

    /**
     * Delete temporary image files
     */
    fun cleanupTempFiles() {
        try {
            context.getExternalFilesDir("images")?.listFiles()?.forEach { file ->
                // Delete files older than 1 day
                val dayInMillis = 24 * 60 * 60 * 1000L
                val now = System.currentTimeMillis()
                if (now - file.lastModified() > dayInMillis) {
                    if (file.delete()) {
                        Timber.d("Deleted old temporary file: ${file.name}")
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error cleaning up temporary files")
        }
    }
}

object ImageUtils {
    fun decodeBase64ToBitmap(base64String: String): android.graphics.Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            logTimber("ImageUtils", "Error decoding base64 to bitmap: ${e.message}")
            null
        }
    }
}