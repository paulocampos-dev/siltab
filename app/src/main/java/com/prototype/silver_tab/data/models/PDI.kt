package com.prototype.silver_tab.data.models

import java.time.LocalDateTime

data class PDI(
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

){

override fun toString(): String {
    return """
            PDI(
                car_id: $car_id,
                inspector_id: $inspector_id,
                inspection_date: $inspection_date,
                chassi_number: $chassi_number,
                chassi_image_path: $chassi_image_path,
                soc_percentage: $soc_percentage,
                soc_percentage_image_path: $soc_percentage_image_path,
                battery_12v: $battery_12v,
                battery_12v_image_path: $battery_12v_image_path,
                tire_pressure_dd: $tire_pressure_dd,
                tire_pressure_de: $tire_pressure_de,
                tire_pressure_td: $tire_pressure_td,
                tire_pressure_te: $tire_pressure_te,
                tire_pressure_image_path: $tire_pressure_image_path,
                five_minutes_hybrid: $five_minutes_hybrid,
                extra_text: $extra_text,
                extra_image_1: $extra_image_1,
                extra_image_2: $extra_image_2,
                extra_image_3: $extra_image_3
            )
        """.trimIndent()
}
}