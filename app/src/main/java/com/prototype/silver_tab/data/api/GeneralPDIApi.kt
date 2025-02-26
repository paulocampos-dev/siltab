package com.prototype.silver_tab.data.api

import com.prototype.silver_tab.data.models.Car
import com.prototype.silver_tab.data.models.CarResponse
import com.prototype.silver_tab.data.models.PDI
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PdiApi {

    @GET("/pdi/dealer/{dealer_code}")
    suspend fun getPdi(
        @Path("dealer_code") dealerCode: String
    ): List<PDI>

    @POST("/pdi/")
    suspend fun postPdi(
        @Body pdi : PDI
    ): Response<PDI>
}

interface CarsApi {

    @GET("/cars/")
    suspend fun getCars(

    ): List <Car>


    @GET("/cars/dealer/{dealer_code}")
    suspend fun getCarsDealer(
        @Path("dealer_code") dealerCode: String
    ) : List<Car>

    @GET("/cars/car_id/{chassi_number}")
    suspend fun getCarId(
        @Path("chassi_number") chassiNumber: String
    ) : Car

    @POST("/cars/")
    suspend fun postCar(
        @Body car : CarResponse
    ): Response<Car>
}
