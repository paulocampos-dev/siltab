package com.prototype.silver_tab.data.mappers

import com.prototype.silver_tab.data.models.Car


fun CarsData(listPdi :List<Car>,) : List <Map<String, String?>> {
    val data = listPdi.map { item ->
         mapOf(
             "Car id" to item.id,
             "Model" to item.model,
             "Year" to item.year.toString(),
            "Chassi" to item.vin
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