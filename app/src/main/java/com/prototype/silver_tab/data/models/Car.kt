package com.prototype.silver_tab.data.models

data class Car (
    val car_id: Int?,
    val model: String,
    val dealer_code: String,
    val chassi_number: String,
    val pdi_ids: List<Int>?
)
