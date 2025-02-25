package com.prototype.silver_tab.data.models

import java.time.LocalDateTime

data class PDI(
    val PDI_id: Int?,
    val car_id: Int?,
    val user_id: Int?,
    val dealer_code: String?,
    val created_at: String, // Usará assim ou com string?
    val chassi_number: String?,
    val soc_percentage: Double?,
    val battery12v: Int?,
    val five_minutes_hybrid: Boolean?,
    val tire_pressure_dd: Double?,
    val tire_pressure_de: Double?,
    val tire_pressure_td: Double?,
    val tire_pressure_te: Double?,
    val extra_text: String?,

){

    override fun toString(): String {
        return """
        PDI(
            PDI_id: $PDI_id,
            car_id: $car_id,
            user_id: $user_id,
            dealer_code: $dealer_code,
            created_at: $created_at,
            chassi_number: $chassi_number,
            soc_percentage: $soc_percentage,
            battery_12v: $battery12v,
            five_minutes_hybrid: $five_minutes_hybrid,
            tire_pressure_dd: $tire_pressure_dd,
            tire_pressure_de: $tire_pressure_de,
            tire_pressure_td: $tire_pressure_td,
            tire_pressure_te: $tire_pressure_te,
            extra_text: $extra_text
        )
    """.trimIndent()
    }
}