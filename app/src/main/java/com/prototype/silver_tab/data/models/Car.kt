package com.prototype.silver_tab.data.models

data class Car (
    val carId: Int,
    val vin: String,
    val carModel: String?,
    val dealerCode: String,
    val is_sold: Boolean?,
)


data class CarResponse (
    val car_id: Int?,
    val car_model_id: Int?,
    val dealer_code: String,
    val vin: String,
    val pdi_ids: List<Int>?,
    val is_sold: Boolean?,
)
