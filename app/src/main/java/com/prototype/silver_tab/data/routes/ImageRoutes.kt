package com.prototype.silver_tab.data.routes

import com.prototype.silver_tab.data.models.ImageDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ImageRoutes {
    // Keep the original image endpoints for now, as they don't need to change
    // The Java backend still exposes these endpoints
    @GET("image/pdi/{pdiId}")
    suspend fun getPdiImages(
        @Path("pdiId") pdiId: Int,
        @Query("pdiImageType") pdiImageType: String? = null
    ): Response<List<ImageDTO>>

    @Multipart
    @POST("image/pdi/{pdi}")
    suspend fun uploadPdiImage(
        @Path("pdi") pdi: Int,
        @Part("pdiImageType") pdiImageType: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<ImageDTO>

    @DELETE("image/pdi/{imageId}")
    suspend fun deletePdiImage(
        @Path("imageId") imageId: Int
    ): Response<Void>
}