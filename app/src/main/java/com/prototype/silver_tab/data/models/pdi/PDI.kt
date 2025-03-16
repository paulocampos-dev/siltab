package com.prototype.silver_tab.data.models.pdi

import com.squareup.moshi.Json

data class PDI(
    @Json(name = "pdi_id") val pdiId: Int,
    @Json(name = "car_id") val carId: Int,
    @Json(name = "create_by_user_id") val createByUserId: Int,
    @Json(name = "last_modified_by_user") val lastModifiedByUser: Int? = null,
    @Json(name = "last_modified_date") val lastModifiedDate: String? = null,
    @Json(name = "created_date") val createdDate: String,
    @Json(name = "soc_percentage") val socPercentage: Float? = null,
    @Json(name = "battery12v_Voltage") val battery12vVoltage: Float? = null,
    @Json(name = "five_minutes_hybrid_check") val fiveMinutesHybridCheck: Boolean? = null,
    @Json(name = "tire_pressure_dd") val tirePressureFrontRight: Float? = null,
    @Json(name = "tire_pressure_de") val tirePressureFrontLeft: Float? = null,
    @Json(name = "tire_pressure_td") val tirePressureRearRight: Float? = null,
    @Json(name = "tire_pressure_te") val tirePressureRearLeft: Float? = null,
    @Json(name = "user_comments") val userComments: String? = null
)
