package com.prototype.silver_tab.data.models.pdi

import com.squareup.moshi.Json

data class PdiRequest(
    @Json(name = "car_id") val carId: Int,
    @Json(name = "create_by_user_id") val createByUserId: Int,
    @Json(name = "created_date") val createdDate: String,
    @Json(name = "soc_percentage") val socPercentage: Double? = null,
    @Json(name = "battery12v_Voltage") val battery12vVoltage: Double? = null,
    @Json(name = "five_minutes_hybrid_check") val fiveMinutesHybridCheck: Boolean? = null,
    @Json(name = "tire_pressure_dd") val tirePressureFrontRight: Double? = null,
    @Json(name = "tire_pressure_de") val tirePressureFrontLeft: Double? = null,
    @Json(name = "tire_pressure_td") val tirePressureRearRight: Double? = null,
    @Json(name = "tire_pressure_te") val tirePressureRearLeft: Double? = null,
    @Json(name = "user_comments") val userComments: String? = null
)
