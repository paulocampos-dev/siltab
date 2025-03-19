package com.prototype.silver_tab.data.models.car

import com.squareup.moshi.Json

/**
 * Response model for a car that has been marked as sold.
 * This exactly matches the response format from the API.
 */
data class SoldCarResponse(
    @Json(name = "car_id") val carId: Int,
    @Json(name = "vin") val vin: String,
    @Json(name = "dealer_code") val dealerCode: String,
    @Json(name = "car_model_id") val carModelId: Int,
    @Json(name = "is_sold") val isSold: Boolean,
    @Json(name = "sold_date") val soldDate: String?
)

/**
 * Convert a SoldCarResponse to a Car model
 */
fun SoldCarResponse.toCar(): Car {
    return Car(
        carId = this.carId,
        vin = this.vin,
        dealerCode = this.dealerCode,
        carModelId = this.carModelId,
        is_sold = this.isSold,
        soldDate = this.soldDate,
        // Set other fields to null since they're not in the response
        carModel = null
    )
}