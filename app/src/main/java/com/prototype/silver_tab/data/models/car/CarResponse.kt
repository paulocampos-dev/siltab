package com.prototype.silver_tab.data.models.car

import com.squareup.moshi.Json

data class CarResponse(
    @Json(name = "car_id") val carId: Int?,
    @Json(name = "car_model_id") val carModelId: Int?,
    @Json(name = "car_model_name") val carModelName: String?,
    @Json(name = "dealer_code") val dealerCode: String,
    @Json(name = "vin") val vin: String,
    @Json(name = "is_sold") val isSold: Boolean?,
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "sold_date") val soldDate: String?,
    @Json(name = "updated_at") val updatedAt: String?
)
