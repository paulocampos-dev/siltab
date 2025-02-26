package com.prototype.silver_tab.data.repository

import com.prototype.silver_tab.data.api.ImageAPI
import com.prototype.silver_tab.data.api.RetrofitClient
import com.prototype.silver_tab.data.models.ImageDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

object ImageRepository {
    private val imageApi: ImageAPI = RetrofitClient.imageapi

    suspend fun uploadImage(pdiId: Int, imageType: RequestBody, file: MultipartBody.Part): Response<ImageDTO> {
        return withContext(Dispatchers.IO) {
            imageApi.uploadDealerImage(pdi = pdiId, pdiImageType = imageType, file = file)
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
}
