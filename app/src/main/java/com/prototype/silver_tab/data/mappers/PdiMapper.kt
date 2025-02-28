package com.prototype.silver_tab.data.mappers

import com.prototype.silver_tab.data.models.PDI

fun PdiData(listPdi: List<PDI>, fields: List<String> = listOf()): List<Map<String, String?>> {
    val data = listPdi.map { item ->
        val fullMap = mapOf(
            "PDI ID" to item.pdi_id?.toString(),
            "Car ID" to item.car_id?.toString(),
            "User ID" to item.create_by_user_id?.toString(),
            "Created At" to item.created_date,
            "SOC Percentage" to item.soc_percentage?.toString(),
            "Battery 12V" to item.battery12v_Voltage?.toString(),
            "Five Minutes Hybrid" to item.five_minutes_hybrid_check?.toString(),
            "Tire Pressure DD" to item.tire_pressure_dd?.toString(),
            "Tire Pressure DE" to item.tire_pressure_de?.toString(),
            "Tire Pressure TD" to item.tire_pressure_td?.toString(),
            "Tire Pressure TE" to item.tire_pressure_te?.toString(),
            "Extra Text" to item.user_comments
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

