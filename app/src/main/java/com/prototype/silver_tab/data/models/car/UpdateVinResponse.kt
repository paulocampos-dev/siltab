package com.prototype.silver_tab.data.models.car

import com.squareup.moshi.Json

data class UpdateVinResponse(
    @Json(name = "car_id") val carId: Int,
    @Json(name = "vin") val vin: String,
    @Json(name = "dealer_code") val dealerCode: String,
    @Json(name = "car_model_id") val carModelId: Int,
    @Json(name = "is_sold") val isSold: Boolean,
    @Json(name = "sold_date") val soldDate: String?
)
