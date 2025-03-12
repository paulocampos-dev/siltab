package com.prototype.silver_tab.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import coil3.decode.DecodeUtils.calculateInSampleSize
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.data.api_connection.RetrofitClient
import com.prototype.silver_tab.data.models.ImageDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

suspend fun convertImageDtoToUri(imageDTO: ImageDTO): Uri? {
    return withContext(Dispatchers.IO) {
        try {
            val context = SilverTabApplication.instance

            if (imageDTO.imageData != null) {
                // If we have base64 image data, decode it with subsampling for memory efficiency
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }

                // First decode with inJustDecodeBounds=true to check dimensions
                val imageBytes = Base64.decode(imageDTO.imageData!!, Base64.DEFAULT)
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
                val fullUrl = "${RetrofitClient.BASE_URL}/${imageDTO.filePath}"

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
            Timber.tag("ImageConversion").e(e, "Error converting image")
            return@withContext null
        }
    }
}


// Helper function to calculate appropriate sampling size
fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}


suspend fun downloadImageToUri(context: Context, imageUrl: String): Uri? {
    return withContext(Dispatchers.IO) {
        try {
            // Create a unique temporary file
            val tempFile = File.createTempFile(
                "image_${System.currentTimeMillis()}_",
                ".jpg",
                context.cacheDir
            )

            // Create an OkHttp client for the download
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            // Create and execute the request
            val request = Request.Builder().url(imageUrl).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e("ImageDownload", "Failed to download image: ${response.code}")
                return@withContext null
            }

            // Write the image data to the file
            response.body?.byteStream()?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // Get a content URI using FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
        } catch (e: Exception) {
            Log.e("ImageDownload", "Error downloading image: ${e.message}", e)
            null
        }
    }
}