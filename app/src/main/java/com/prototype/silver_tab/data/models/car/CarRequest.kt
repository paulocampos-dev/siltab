package com.prototype.silver_tab.data.models.car

data class CarRequest(
    val carModelId: Int?,
    val dealerCode: String,
    val vin: String,
    val isSold: Boolean = false
)

