package com.prototype.silver_tab.data.models.car

import com.squareup.moshi.Json

data class VinUpdateRequest(
    @Json(name = "carId") val carId: Int,
    @Json(name = "vin") val vin: String
)

