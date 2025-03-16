package com.prototype.silver_tab.utils

import android.util.Log
import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.car.Car
import com.prototype.silver_tab.data.models.car.CarResponse
import com.prototype.silver_tab.data.models.pdi.PDI
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Define maps at the package level
private val nameToId = mapOf(
    "BYD YUAN PLUS" to 1,
    "BYD TAN" to 2,
    "BYD YUAN PRO" to 3,
    "BYD SEAL" to 4,
    "BYD HAN" to 5,
    "BYD DOLPHIN PLUS" to 6,
    "BYD DOLPHIN" to 7,
    "BYD DOLPHIN MINI" to 8,
    "BYD SONG PRO DM-I" to 9,
    "SONG PLUS PREMIUM DM-I" to 10,
    "BYD SONG PLUS DM-I" to 11,
    "BYD KING DM-I" to 12,
    "BYD SHARK" to 13
)

// Create the reverse mapping
private val idToName: Map<Int, String> = nameToId.entries.associateBy({ it.value }, { it.key })

fun getModelIdFromName(carName: String): Int? {
    val normalizedName = carName.trim().uppercase()
    return nameToId[normalizedName]
}

fun getModelNameFromId(modelId: Int): String? {
    return idToName[modelId]
}


fun convertResponseToCar(response: CarResponse): Car {
    return Car(
        carId = response.carId ?: 0,
        vin = response.vin,
        carModel = getModelNameFromId(response.carModelId ?: 0),
        dealerCode = response.dealerCode,
        is_sold = response.isSold
    )
}

fun convertResponsesToCars(responses: List<CarResponse>): List<Car> {
    return responses.map { convertResponseToCar(it) }
}


fun convertPdiToInspectionInfo(pdi: PDI, car: Car): InspectionInfo {
    try {
        return InspectionInfo(
            carId = car.carId,
            vin = car.vin,
            type = determineCarTypeFromModel(car.carModel ?: ""),
            name = getModelNameFromId(car.carId),
            date = pdi.lastModifiedDate ?: pdi.createdDate,
            soc = pdi.socPercentage,
            battery12v = pdi.battery12vVoltage,
            frontLeftTire = pdi.tirePressureFrontLeft,
            frontRightTire = pdi.tirePressureFrontRight,
            rearLeftTire = pdi.tirePressureRearLeft,
            rearRightTire = pdi.tirePressureRearRight,
            comments = pdi.userComments,
            dealerCode = car.dealerCode
        )
    } catch (e: Exception) {
        logTimber(tag = "convertPdiToInspectionInfo", text = "Error converting PDI to InspectionInfo: ${e.message}")
        // Return a basic object with at least the IDs if conversion fails
        return InspectionInfo(
            carId = car.carId,
            vin = car.vin,
            name = determineCarTypeFromModel(car.carModel ?: ""),
            pdiId = pdi.pdiId
        )
    }
}


// Determine car type from its model name
fun determineCarTypeFromModel(model: String): String {
    return when {
        model.contains("HYBRID", ignoreCase = true) -> "Hybrid"
        model.contains("TAN", ignoreCase = true) -> "Hybrid"
        model.contains("DM-I", ignoreCase = true) -> "Hybrid"
        model.contains("SONG", ignoreCase = true) -> "Hybrid"
        model.contains("KING", ignoreCase = true) -> "Hybrid"
        model.contains("SHARK", ignoreCase = true) -> "Hybrid"
        else -> "Electric"
    }
}


// Function to get the appropriate image resource for the car model
fun getCarImageResource(modelName: String?): Int {
    return when (modelName) {
        "BYD YUAN PLUS" -> R.drawable.byd_yuan_plus
        "BYD TAN" -> R.drawable.byd_tan
        "BYD YUAN PRO" -> R.drawable.byd_yuan_pro
        "BYD SEAL" -> R.drawable.pid_car
        "BYD HAN" -> R.drawable.byd_han
        "BYD DOLPHIN PLUS" -> R.drawable.byd_dolphin_plus
        "BYD DOLPHIN" -> R.drawable.byd_dolphin
        "BYD DOLPHIN MINI" -> R.drawable.byd_dolphin_mini
        "BYD SONG PRO DM-I" -> R.drawable.byd_song_pro
        "SONG PLUS PREMIUM DM-I", "BYD SONG PLUS DM-I" -> R.drawable.byd_song_premium
        "BYD KING DM-I" -> R.drawable.byd_king
        "BYD SHARK" -> R.drawable.byd_shark
        else -> R.drawable.pid_car
    }
}

fun logTimber(tag: String, text: String) {
    Log.d(tag, text)
    Timber.tag(tag).d(text)
}

fun logTimberError(tag: String, text: String) {
    Log.e(tag, text)
    Timber.tag(tag).e(text)
}


