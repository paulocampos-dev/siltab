package com.prototype.silver_tab.data.models

import java.time.LocalDateTime

data class PDI(
    val pdi_id: Int?,
    val car_id: Int?,
    val user_id: Int?,
    val dealer_code: String?,
    val created_date: String, // Nome corrigido para coincidir com a API
    val soc_percentage: Double?,
    val battery12v_Voltage: Double?, // Nome corrigido para coincidir com a API
    val five_minutes_hybrid_check: Boolean?, // Nome corrigido
    val tire_pressure_dd: Double?,
    val tire_pressure_de: Double?,
    val tire_pressure_td: Double?,
    val tire_pressure_te: Double?,
    val extra_text: String?,
){

    override fun toString(): String {
        return """
        PDI(
            PDI_id: $pdi_id,
            car_id: $car_id,
            user_id: $user_id,
            dealer_code: $dealer_code,
            created_at: $created_date,
            soc_percentage: $soc_percentage,
            battery_12v: $battery12v_Voltage,
            five_minutes_hybrid: $five_minutes_hybrid_check,
            tire_pressure_dd: $tire_pressure_dd,
            tire_pressure_de: $tire_pressure_de,
            tire_pressure_td: $tire_pressure_td,
            tire_pressure_te: $tire_pressure_te,
            extra_text: $extra_text
        )
    """.trimIndent()
    }
}