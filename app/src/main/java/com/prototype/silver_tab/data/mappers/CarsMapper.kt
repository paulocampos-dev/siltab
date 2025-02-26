package com.prototype.silver_tab.data.mappers

import com.prototype.silver_tab.data.models.Car


fun CarsData(listPdi :List<Car>,) : List <Map<String, String?>> {
    val data = listPdi.map { item ->
         mapOf(
             "Car ID" to item.car_id.toString(),
             "Chassi" to item.chassi_number,
             "Dealer code" to item.dealer_code,
             "Model" to item.car_model_name,
             "PDIS_ids" to item.pdi_ids?.joinToString(",")
        )

    }
    return data
}
fun CarsDataMapped (carsData: List <Map<String, String?>>, fields: List<String> = listOf()): List<Map<String, String?>> {
    return carsData.map { item ->
        if (fields.isNotEmpty()) {
            item.filterKeys { it in fields }
        } else {
            item
        }
    }
}