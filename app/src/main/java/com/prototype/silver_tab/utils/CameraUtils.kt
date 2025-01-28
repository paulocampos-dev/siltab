package com.prototype.silver_tab.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraUtils(private val context: Context) {
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        // Get the app's private directory for storing images
        val storageDir = context.getExternalFilesDir("Images")

        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",        /* suffix */
            storageDir     /* directory */
        )
    }

    fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}