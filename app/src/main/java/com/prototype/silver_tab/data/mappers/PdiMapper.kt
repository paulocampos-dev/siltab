package com.prototype.silver_tab.data.mappers

import com.prototype.silver_tab.data.models.PDI

fun PdiData(listPdi: List<PDI>, fields: List<String> = listOf()): List<Map<String, String?>> {
    val data = listPdi.map { item ->
        val fullMap = mapOf(
            "PDI ID" to item.PDI_id?.toString(),
            "Car ID" to item.car_id?.toString(),
            "User ID" to item.user_id?.toString(),
            "Dealer Code" to item.dealer_code,
            "Created At" to item.createdAt,
            "Chassi Number" to item.chassi_number,
            "Chassi Image ID" to item.chassi_image_id?.toString(),
            "SOC Percentage" to item.soc_percentage?.toString(),
            "SOC Percentage Image ID" to item.soc_percentage_image_id?.toString(),
            "Battery 12V" to item.battery_12v?.toString(),
            "Battery 12V Image ID" to item.battery_12v_image_id?.toString(),
            "Five Minutes Hybrid" to item.five_minutes_hybrid?.toString(),
            "Tire Pressure DD" to item.tire_pressure_dd?.toString(),
            "Tire Pressure DE" to item.tire_pressure_de?.toString(),
            "Tire Pressure TD" to item.tire_pressure_td?.toString(),
            "Tire Pressure TE" to item.tire_pressure_te?.toString(),
            "Tire Pressure Image ID" to item.tire_pressure_image_id?.toString(),
            "Extra Image 1" to item.extra_image_id?.toString(),
            "Extra Image 2" to item.extra2_image_id?.toString(),
            "Extra Text" to item.extra_text
        )
        if (fields.isNotEmpty()) {
            fullMap.filterKeys { it in fields }
        } else {
            fullMap
        }
    }

    return data
}
fun PdiDataFiltered(pdiData: List<Map<String, String?>>, fields: List<String> = listOf()): List<Map<String, String?>> {
    return pdiData.map { item ->
        if (fields.isNotEmpty()) {
            item.filterKeys { it in fields }
        } else {
            item
        }
    }
}

