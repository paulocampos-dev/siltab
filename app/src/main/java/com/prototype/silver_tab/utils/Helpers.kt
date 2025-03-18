package com.prototype.silver_tab.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.car.Car
import com.prototype.silver_tab.data.models.car.CarResponse
import com.prototype.silver_tab.data.models.pdi.PDI
import com.prototype.silver_tab.language.StringResources
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber
import timber.log.Timber.Forest.tag
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

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

fun getModelNameFromId(modelId: Int): String {
    return idToName[modelId] ?: "Unknow Model"
}


fun convertResponseToCar(response: CarResponse): Car {
    logTimber("Helper", "Model: ${response.carModelName}")
    return Car(
        carId = response.carId ?: 0,
        vin = response.vin,
        carModel = response.carModelName,
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
            pdiId = pdi.pdiId,
            carId = car.carId,
            vin = car.vin,
            type = determineCarTypeFromModel(car.carModel ?: ""),
            name = car.carModel ?: "Unknow Car Model",
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
fun getCarImageResource(modelName: String): Int {
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
        "BYD SONG PLUS DM-I" -> R.drawable.byd_song_plus
        "SONG PLUS PREMIUM DM-I" -> R.drawable.byd_song_premium
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

// Helper method to check if the error is specifically about no PDI records found
fun isNoPdiRecordsError(response: Response<List<PDI>>): Boolean {
    if (response.code() != 500) return false

    try {
        val errorBody = response.errorBody()?.string() ?: return false
        val errorJson = JSONObject(errorBody)
        val errorMessage = errorJson.optString("error", "")

        return errorMessage.contains("No PDI records found for this dealer") ||
                errorMessage.contains("404 Not Found")
    } catch (e: Exception) {
        Timber.e(e, "Error parsing error response")
        return false
    }
}

// Helper method to check if the error is specifically about no cars found
fun isNoCarsFoundError(response: Response<List<CarResponse>>): Boolean {
    if (response.code() != 404 && response.code() != 500) return false

    try {
        val errorBody = response.errorBody()?.string() ?: return false

        // Check for common "no records" error messages
        if (errorBody.contains("No cars found") ||
            errorBody.contains("No records found") ||
            errorBody.contains("not found") ||
            errorBody.contains("404") ||
            errorBody.contains("empty")) {
            return true
        }

        // Try to parse as JSON
        try {
            val errorJson = JSONObject(errorBody)
            val errorMessage = errorJson.optString("error", "")

            return errorMessage.contains("No cars found") ||
                    errorMessage.contains("not found") ||
                    errorMessage.contains("404")
        } catch (e: Exception) {
            // Couldn't parse as JSON, but it might still be a "no records" error
            return false
        }
    } catch (e: Exception) {
        Timber.e(e, "Error parsing error response")
        return false
    }
}

fun validateNumericInput(input: String): String {
    // Replace commas with dots and filter out invalid characters
    val processedValue = input.replace(',', '.').filter { it.isDigit() || it == '.' }

    // Ensure only one decimal point
    val singleDecimalValue = if (processedValue.count { it == '.' } > 1) {
        val firstDotIndex = processedValue.indexOf('.')
        processedValue.substring(0, firstDotIndex + 1) +
                processedValue.substring(firstDotIndex + 1).replace(".", "")
    } else {
        processedValue
    }

    // Limit to two decimal places
    val decimalPointIndex = singleDecimalValue.indexOf('.')
    return if (decimalPointIndex != -1 && singleDecimalValue.length > decimalPointIndex + 3) {
        // Keep only two digits after decimal point
        singleDecimalValue.substring(0, decimalPointIndex + 3)
    } else {
        singleDecimalValue
    }
}

fun formatRelativeDate(
    dateStr: String,
    strings: StringResources
): String {
    try {
        // Try parsing both ISO datetime and date formats
        val date = try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateStr)
        } catch (e: Exception) {
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
            } catch (e: Exception) {
                return dateStr // Return original if parsing fails
            }
        } ?: return dateStr

        val today = Calendar.getInstance()
        val dateCalendar = Calendar.getInstance()
        dateCalendar.time = date

        // Calculate difference in days
        val diffInMillis = abs(today.timeInMillis - dateCalendar.timeInMillis)
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

        // Calculate months difference
        val yearDiff = today.get(Calendar.YEAR) - dateCalendar.get(Calendar.YEAR)
        val monthDiff = today.get(Calendar.MONTH) - dateCalendar.get(Calendar.MONTH)
        val totalMonthsDiff = yearDiff * 12 + monthDiff

        return when {
            // If more than 1 months ago, show formatted date
            totalMonthsDiff > 1 -> {
                SimpleDateFormat("dd/MM/yyyy' 'HH:mm", Locale.getDefault()).format(date)
            }
            // If within last 3 months, show relative date
            diffInDays == 0 -> strings.today
            diffInDays == 1 -> strings.yesterday
            else -> "$diffInDays ${strings.daysAgo}"
        }
    } catch (e: Exception) {
        return dateStr
    }
}

// Helper methods for file handling
fun getFileFromUri(context: Context, uri: Uri): File? {
    return try {
        // Create a temporary file
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)

        // Copy content from URI to the file
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        tempFile
    } catch (e: Exception) {
        logTimberError("ImageRepository", "Error getting file from URI: ${e.message}")
        null
    }
}

fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null

    // Try to get the file name from the content provider
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                result = cursor.getString(nameIndex)
            }
        }
    }

    // If we couldn't get it from the content provider, extract from the URI
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut!! + 1)
        }
    }

    return result
}

fun getMimeType(context: Context, uri: Uri): String? {
    return context.contentResolver.getType(uri)
}
