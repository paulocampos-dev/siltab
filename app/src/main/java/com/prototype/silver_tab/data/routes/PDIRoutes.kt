package com.prototype.silver_tab.data.routes

import com.prototype.silver_tab.data.models.pdi.PDI
import com.prototype.silver_tab.data.models.pdi.PdiRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PdiRoutes {
    @GET("pdi-api/pdi")
    suspend fun getAllPdis(): Response<List<PDI>>

    @GET("pdi-api/pdi/dealer/{dealerCode}")
    suspend fun getPdisByDealer(
        @Path("dealerCode") dealerCode: String
    ): Response<List<PDI>>

    @POST("pdi-api/pdi")
    suspend fun createPdi(
        @Body pdiRequest: PdiRequest
    ): Response<PDI>

    @PUT("pdi-api/pdi/{pdiId}")
    suspend fun updatePdi(
        @Path("pdiId") pdiId: Int,
        @Body pdiRequest: PdiRequest
    ): Response<PDI>


    @PUT("pdi-api/pdi/pending/{pdiId}")
    suspend fun changePendingStatus(
        @Path("pdiId") pdiId: Int,
        @Body newSoc: Map<String, Float>
    ): Response<PDI>

    // seria bom usar para ficar future proof
//    @GET("pdi-api/car-models")
//    suspend fun getCarModels(): Response<List<CarModel>>
}

