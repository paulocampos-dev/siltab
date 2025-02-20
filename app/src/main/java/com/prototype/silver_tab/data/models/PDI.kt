package com.prototype.silver_tab.data.models

import java.time.LocalDateTime

data class PDI(
    val PDI_id: Int?,
    val car_id: Int?,
    val user_id: Int?,
    val dealer_code: String?,
    val createdAt: String, // Usará assim ou com string?
    val chassi_number: String?,
    val chassi_image_id: Int?,
    val soc_percentage: Double?,
    val soc_percentage_image_id: Int?,
    val battery_12v: Int?,
    val battery_12v_image_id: Int?,
    val five_minutes_hybrid: Boolean?,
    val tire_pressure_dd: Double?,
    val tire_pressure_de: Double?,
    val tire_pressure_td: Double?,
    val tire_pressure_te: Double?,
    val tire_pressure_image_id: Int?,
    val extra_image_id: Int?,
    val extra2_image_id: Int?,
    val extra_text: String?,

){

    override fun toString(): String {
        return """
        PDI(
            PDI_id: $PDI_id,
            car_id: $car_id,
            user_id: $user_id,
            dealer_code: $dealer_code,
            createdAt: $createdAt,
            chassi_number: $chassi_number,
            chassi_image_id: $chassi_image_id,
            soc_percentage: $soc_percentage,
            soc_percentage_image_id: $soc_percentage_image_id,
            battery_12v: $battery_12v,
            battery_12v_image_id: $battery_12v_image_id,
            five_minutes_hybrid: $five_minutes_hybrid,
            tire_pressure_dd: $tire_pressure_dd,
            tire_pressure_de: $tire_pressure_de,
            tire_pressure_td: $tire_pressure_td,
            tire_pressure_te: $tire_pressure_te,
            tire_pressure_image_id: $tire_pressure_image_id,
            extra_image_id: $extra_image_id,
            extra2_image_id: $extra2_image_id,
            extra_text: $extra_text
        )
    """.trimIndent()
    }
}