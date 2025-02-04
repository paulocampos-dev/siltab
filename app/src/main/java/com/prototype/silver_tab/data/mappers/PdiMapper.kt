package com.prototype.silver_tab.data.mappers

import com.prototype.silver_tab.data.models.PDI

fun PdiData(listPdi :List<PDI>, fields: List<String> = listOf()) : List <Map<String, String?>> {
    val data = listPdi.map { item ->
        val fullMap = mapOf(
            "Car ID" to item.car_id,
            "Inspector ID" to item.inspector_id?.toString(),
            "Inspection Date" to item.inspection_date,
            "Chassi Number" to item.chassi_number?.toString(),
            "Chassi Image Path" to item.chassi_image_path,
            "SOC Percentage" to item.soc_percentage?.toString(),
            "SOC Percentage Image Path" to item.soc_percentage_image_path,
            "Battery 12V" to item.battery_12v?.toString(),
            "Battery 12V Image Path" to item.battery_12v_image_path,
            "Tire Pressure DD" to item.tire_pressure_dd?.toString(),
            "Tire Pressure DE" to item.tire_pressure_de?.toString(),
            "Tire Pressure TD" to item.tire_pressure_td?.toString(),
            "Tire Pressure TE" to item.tire_pressure_te?.toString(),
            "Tire Pressure Image Path" to item.tire_pressure_image_path,
            "Five Minutes Hybrid" to item.five_minutes_hybrid?.toString(),
            "Extra Text" to item.extra_text,
            "Extra Image 1" to item.extra_image_1,
            "Extra Image 2" to item.extra_image_2,
            "Extra Image 3" to item.extra_image_3
        )
        if (fields.isNotEmpty()){
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

