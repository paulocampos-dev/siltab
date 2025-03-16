package com.prototype.silver_tab.data.models.car

data class CarResponse(
    val carId: Int?,
    val carModelId: Int?,
    val dealerCode: String,
    val vin: String,
    val isSold: Boolean?,
    val createdAt: String?,
    val updatedAt: String?
)
