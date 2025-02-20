package com.prototype.silver_tab.data.models

data class Car (

    val car_id: String,
    val chassi_number: String,
    val model: String,
    val dealer_Code: String,
    val PDI_IDS: List<Int>
)
