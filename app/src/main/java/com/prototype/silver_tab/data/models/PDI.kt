package com.prototype.silver_tab.data.models

import java.time.LocalDateTime

data class PDI(

    val id: Int,
    val car_id: String?,
    val inspector_id: Int?,
    val inspection_date: String, // Usará assim ou com string?
    val chassi_number: Int?,
    val chassi_image_path: String?,
    val soc_percentage: Double?,
    val soc_percentage_image_path: String?,
    val battery_12v: Double?,
    val battery_12v_image_path: String?,
    val tire_pressure_dd: Double?,
    val tire_pressure_de: Double?,
    val tire_pressure_td: Double?,
    val tire_pressure_te: Double?,
    val tire_pressure_image_path: String?,
    val five_minutes_hybrid: Boolean?,
    val extra_text: String?,
    val extra_image_1: String?,
    val extra_image_2: String?,
    val extra_image_3: String?

)