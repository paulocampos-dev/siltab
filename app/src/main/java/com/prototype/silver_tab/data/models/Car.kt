package com.prototype.silver_tab.data.models

data class Car (
    val car_id: Int?,
    val car_model_name: String?,
    val dealer_code: String,
    val chassi_number: String,
    val pdi_ids: List<Int>?
)


data class CarResponse (
    val car_id: Int?,
    val car_model_id: Int?,
    val dealer_code: String,
    val chassi_number: String,
    val pdi_ids: List<Int>?
)
