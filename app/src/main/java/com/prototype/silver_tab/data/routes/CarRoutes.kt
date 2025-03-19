package com.prototype.silver_tab.data.routes

import com.prototype.silver_tab.data.models.car.Car
import com.prototype.silver_tab.data.models.car.CarResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CarRoutes {
    @GET("pdi-api/cars")
    suspend fun getCars(): Response<List<Car>>

    @GET("pdi-api/cars/dealer/{dealerCode}")
    suspend fun getCarByDealerCode(
        @Path("dealerCode") dealerCode: String
    ): Response<List<CarResponse>>

    @GET("pdi-api/cars/carId/{id}")
    suspend fun getCarById(
        @Path("id") vin: String
    ): Response<CarResponse>

    @GET("pdi-api/cars/{vin}")
    suspend fun getCarByVin(
        @Path("vin") vin: String
    ): Response<CarResponse>

    @POST("pdi-api/cars")
    suspend fun postCar(
        @Body car: CarResponse
    ): Response<CarResponse>

    @PUT("pdi-api/cars/{vin}/sold")
    suspend fun markCarAsSold(
        @Path("vin") vin: String,
        @Body soldData: Map<String, String>
    ): Response<Car>

    @PUT("pdi-api/cars/changeVin/{car_id}")
    suspend fun changeWrongVin(
        @Path("car_id") carId: Int,
        @Body newVin: Map<String, String>
    ): Response<Car>
}