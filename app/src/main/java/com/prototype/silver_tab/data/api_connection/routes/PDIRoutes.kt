package com.prototype.silver_tab.data.api_connection.routes

import com.prototype.silver_tab.data.models.Car
import com.prototype.silver_tab.data.models.CarResponse
import com.prototype.silver_tab.data.models.PDI
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PdiApi {
    // Now using the new endpoint from the Java backend
    @GET("pdi-api/pdi/dealer/{dealerCode}")
    suspend fun getPdi(
        @Path("dealerCode") dealerCode: String
    ): List<PDI>

    @POST("pdi-api/pdi")
    suspend fun postPdi(
        @Body pdi: PDI
    ): Response<PDI>

    @PUT("pdi-api/pdi/{pdi_id}")
    suspend fun updatePdi(
        @Path("pdi_id") pdiId: Int,
        @Body pdi: PDI
    ): Response<PDI>
}

interface CarsApi {
    @GET("pdi-api/cars")
    suspend fun getCars(): List<Car>

    @GET("pdi-api/cars/dealer/{dealerCode}")
    suspend fun getCarsDealer(
        @Path("dealerCode") dealerCode: String
    ): List<Car>

    @GET("pdi-api/cars/carId/{vin}")
    suspend fun getCarId(
        @Path("vin") vin: String
    ): Car

    @GET("pdi-api/cars/{vin}")
    suspend fun getCarByVin(
        @Path("vin") vin: String
    ): Car

    @POST("pdi-api/cars")
    suspend fun postCar(
        @Body car: CarResponse
    ): Response<Car>

    @PUT("pdi-api/cars/{vin}/sold")
    suspend fun markCarAsSold(
        @Path("vin") vin: String,
        @Body soldData: Map<String, String>
    ): Response<Car>
}