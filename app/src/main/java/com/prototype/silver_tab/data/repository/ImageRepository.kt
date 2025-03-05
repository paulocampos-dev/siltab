package com.prototype.silver_tab.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.prototype.silver_tab.data.api.AuthManager
import com.prototype.silver_tab.data.api.ImageAPI
import com.prototype.silver_tab.data.api.RetrofitClient
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

object ImageRepository {
    private val imageApi: ImageAPI = RetrofitClient.imageApi

    suspend fun uploadImage(pdiId: Int, imageType: RequestBody, file: MultipartBody.Part): Response<ImageDTO> {
        return withContext(Dispatchers.IO) {
            imageApi.uploadPdiImage(pdi = pdiId, pdiImageType = imageType, file = file)
        }
    }

    suspend fun getAllPdiImages(pdiId: Int): List<ImageDTO>? {
        return withContext(Dispatchers.IO) {
            val response = imageApi.getPdiImages(pdiId = pdiId, pdiImageType = null)
            if (response.isSuccessful) response.body() else null
        }
    }
    suspend fun getPdiImagesByTypeName(pdiId: Int, pdiImageTypeName: String): List<ImageDTO>? {
        return withContext(Dispatchers.IO) {
            val response = imageApi.getPdiImages(pdiId = pdiId, pdiImageType = pdiImageTypeName)
            if (response.isSuccessful) response.body() else null
        }
    }

    suspend fun uploadImages(
        context: Context,
        uris: List<Uri>,
        pdiId: Int,
        imageType: String
        )
    {

        if(uris.isEmpty()){
            return
        }
        for (uri in uris){
            try{
                if (AuthManager.getAccessToken().isNullOrEmpty()){
                    withContext(Dispatchers.Main){
//                        var uploadResult = "Error: No authentication token available."
                        Log.d("ERRO_AUTH", "ERROU")
                    }
                }
                val tempFile = FileUtils.getFileFromUri(context, uri) ?: continue
                if (!tempFile.exists()) continue
                val fileName = FileUtils.getFileName(context, uri)
                val mimeType = FileUtils.getMimeType(context, uri) ?: ""

                val requestFile = tempFile.asRequestBody(mimeType.toMediaType())
                val multpartBody= MultipartBody.Part.createFormData("file", fileName, requestFile)
                val response = uploadImage(
                    pdiId = pdiId,
                    imageType = imageType.toRequestBody("text/plain".toMediaTypeOrNull()),
                    file = multpartBody
                )
                if (response.isSuccessful){
                    Log.d("UPLOAD_IMAGE", "Imagem '$fileName' enviada com sucesso!")
                    //Colocar para aparecer mais infos aqui
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UPLOAD_IMAGE", "Falha no upload: ${response.code()} $errorBody")
                }




            } catch (e: Exception) {
                Log.e("UPLOAD_IMAGE", "Erro ao enviar imagem: ${e.message}")
            }

        }


    }
}




//coroutineScope.launch {
//    if (selectedImageUri == null) {
//        withContext(Dispatchers.Main) { uploadResult = "No image selected" }
//        return@launch
//    }
//    try {
//        if (AuthManager.getAccessToken().isNullOrEmpty()) {
//            withContext(Dispatchers.Main) {
//                uploadResult = "Error: No authentication token available. Please login first."
//            }
//            return@launch
//        }
//        val tempFile = FileUtils.getFileFromUri(context, selectedImageUri!!)
//        if (tempFile == null || !tempFile.exists()) {
//            withContext(Dispatchers.Main) { uploadResult = "Error: Unable to read the selected file." }
//            return@launch
//        }
//        val fileName = FileUtils.getFileName(context, selectedImageUri!!) ?: "unknown.jpg"
//        val mimeType = FileUtils.getMimeType(context, selectedImageUri!!) ?: "image/jpeg"
//
//        val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
//        val multipartBody = MultipartBody.Part.createFormData("file", fileName, requestFile)
//        val imageTypeBody = "CHASSI".toRequestBody("text/plain".toMediaTypeOrNull())
//
//        val response = ImageRepository.uploadImage(
//            pdiId = 1,
//            imageType = imageTypeBody,
//            file = multipartBody
//        )
//
//        withContext(Dispatchers.Main) {
//            uploadResult = if (response.isSuccessful) {
//                "Upload successful! Image ID: ${response.body()?.imageId}"
//            } else {
//                val errorMessage = try {
//                    response.errorBody()?.string()
//                } catch (e: Exception) {
//                    "Error reading error body"
//                }
//                "Upload failed: ${response.code()} $errorMessage"
//            }
//        }
//    } catch (e: Exception) {
//        withContext(Dispatchers.Main) {
//            uploadResult = "Upload error: ${e.message}\n${e.stackTraceToString()}"
//        }
//    }
//}
//}