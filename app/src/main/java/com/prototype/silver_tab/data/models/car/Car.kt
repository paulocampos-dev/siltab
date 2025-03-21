package com.prototype.silver_tab.data.models.car

data class Car (
    val carId: Int,
    val vin: String,
    val carModel: String?,
    val carModelId: Int? = null,
    val dealerCode: String,
    val is_sold: Boolean?,
    val soldDate: String?
)