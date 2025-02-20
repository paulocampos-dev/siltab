package com.prototype.silver_tab.data.mappers

import com.prototype.silver_tab.data.models.Car


fun CarsData(listPdi :List<Car>,) : List <Map<String, String?>> {
    val data = listPdi.map { item ->
         mapOf(
             "Car id" to item.car_id,
             "Model" to item.model,
            "Chassi" to item.chassi_number,
             "Dealer code" to item.dealer_Code,
             "PDIS_ids" to item.PDI_IDS.toString()
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