package com.prototype.silver_tab.data.api

import com.prototype.silver_tab.data.models.Car
import com.prototype.silver_tab.data.models.PDI
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PdiApi {

    @GET("/pdi/inspector/1")
    suspend fun getPdi(

    ): List<PDI>

    @POST("/pdi/")
    suspend fun postPdi(
        @Body pdi : PDI
    ): Response<PDI>
}

interface CarsApi {

    @GET("/cars")
    suspend fun getCars(

    ): List <Car>
}
