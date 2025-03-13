package com.prototype.silver_tab.data.repository

import android.content.Context
import android.net.Uri
import com.prototype.silver_tab.data.api_connection.RetrofitClient
import com.prototype.silver_tab.data.models.CarResponse
import com.prototype.silver_tab.data.models.PDI
import com.prototype.silver_tab.viewmodels.CheckScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Repository class that handles all data operations for the Check Screen.
 * This separates the data layer from the UI and ViewModel.
 */
class CheckScreenRepository(
    private val imageRepository: ImageRepository = ImageRepository
) {
    /**
     * Submit a new PDI
     * @param state The current check screen state
     * @param context Android context
     * @param userId The ID of the user submitting the PDI
     * @param dealerCode The dealer code
     * @param modelId Optional model ID for the car
     * @return The ID of the created PDI or null if the operation failed
     */
    suspend fun submitNewPdi(
        state: CheckScreenState,
        context: Context,
        userId: Long,
        dealerCode: String,
        modelId: Int? = null
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            // First, create or get car ID
            val carId = if (state.chassisNumber.isNotBlank()) {
                // Try to find existing car with this VIN
                val existingCarId = getCarIdByVin(state.chassisNumber)

                // If car doesn't exist, create a new one
                existingCarId ?: createNewCar(state.chassisNumber, dealerCode, modelId)
            } else {
                return@withContext Result.failure(Exception("Chassis number cannot be empty"))
            }

            if (carId == null) {
                return@withContext Result.failure(Exception("Failed to create or find car"))
            }

            // Now create the PDI
            val pdiId = createPdi(state, carId, userId)
                ?: return@withContext Result.failure(Exception("Failed to create PDI"))

            Result.success(pdiId)
        } catch (e: Exception) {
            Timber.e(e, "Error submitting new PDI")
            Result.failure(e)
        }
    }

    /**
     * Update an existing PDI
     * @param pdiId The ID of the PDI to update
     * @param state The current check screen state
     * @param context Android context
     * @param userId The ID of the user updating the PDI
     * @return Success or failure result
     */
    suspend fun updatePdi(
        pdiId: Int,
        state: CheckScreenState,
        context: Context,
        userId: Long,
        deletedImageIds: Set<Int> = emptySet()
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val pdi = PDI(
                pdi_id = pdiId,
                car_id = null, // Not needed for update
                create_by_user_id = userId.toInt(),
                created_date = formatDateTime(LocalDateTime.now()),
                soc_percentage = state.socPercentage.toDoubleOrNull() ?: 0.0,
                battery12v_Voltage = state.batteryVoltage.toDoubleOrNull() ?: 0.0,
                tire_pressure_dd = state.frontRightPressure.toDoubleOrNull() ?: 0.0,
                tire_pressure_de = state.frontLeftPressure.toDoubleOrNull() ?: 0.0,
                tire_pressure_td = state.rearRightPressure.toDoubleOrNull() ?: 0.0,
                tire_pressure_te = state.rearLeftPressure.toDoubleOrNull() ?: 0.0,
                five_minutes_hybrid_check = state.isCarStarted,
                user_comments = state.additionalInfo
            )

            val response = RetrofitClient.pdiApi.updatePdi(pdiId, pdi)

            if (response.isSuccessful) {
                // Handle deleted images if any
                if (deletedImageIds.isNotEmpty()) {
                    deletePdiImages(deletedImageIds)
                }

                Result.success(true)
            } else {
                val errorMessage = "Failed to update PDI: ${response.code()} ${response.message()}"
                Timber.e(errorMessage)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating PDI")
            Result.failure(e)
        }
    }

    /**
     * Upload all images for a PDI
     * @param pdiId The ID of the PDI to attach images to
     * @param state The current check screen state
     * @param context Android context
     * @return Success or failure result
     */
    suspend fun uploadPdiImages(
        pdiId: Int,
        state: CheckScreenState,
        context: Context
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            uploadImagesForType(context, pdiId, state.chassisImageUris, "vin")
            uploadImagesForType(context, pdiId, state.socImageUris, "soc")
            uploadImagesForType(context, pdiId, state.battery12VImageUris, "battery12V")
            uploadImagesForType(context, pdiId, state.tirePressureImageUris, "tire")
            uploadImagesForType(context, pdiId, state.extraImageUris, "extraImages")

            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error uploading PDI images")
            Result.failure(e)
        }
    }

    /**
     * Delete PDI images by their IDs
     * @param imageIds Set of image IDs to delete
     * @return Map of image IDs to deletion success status
     */
    suspend fun deletePdiImages(imageIds: Set<Int>): Result<Map<Int, Boolean>> =
        withContext(Dispatchers.IO) {
            try {
                val results = imageRepository.deletePdiImages(imageIds)
                Result.success(results)
            } catch (e: Exception) {
                Timber.e(e, "Error deleting PDI images")
                Result.failure(e)
            }
        }

    /**
     * Check if a VIN already exists in the database
     * @param vin The VIN to check
     * @return True if the VIN exists, false otherwise
     */
    suspend fun vinExists(vin: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val carId = getCarIdByVin(vin)
            Result.success(carId != null)
        } catch (e: Exception) {
            // If we get a 404, the VIN doesn't exist
            if (e.message?.contains("404") == true) {
                Result.success(false)
            } else {
                Timber.e(e, "Error checking if VIN exists")
                Result.failure(e)
            }
        }
    }

    /* Private helper methods */

    private suspend fun createNewCar(
        vin: String,
        dealerCode: String,
        modelId: Int?
    ): Int? = withContext(Dispatchers.IO) {
        try {
            val car = CarResponse(
                car_id = null,
                car_model_id = modelId,
                dealer_code = dealerCode,
                vin = vin,
                pdi_ids = null,
                is_sold = false
            )

            val response = RetrofitClient.carsApi.postCar(car)
            if (response.isSuccessful) {
                val createdCar = response.body()
                Timber.d("Car created successfully! car_id: ${createdCar?.car_id}")
                createdCar?.car_id
            } else {
                val errorBody = response.errorBody()?.string()
                Timber.e("Error creating car: ${response.code()} $errorBody")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error creating car")
            null
        }
    }

    private suspend fun getCarIdByVin(vin: String): Int? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.carsApi.getCarId(vin)
            response.car_id
        } catch (e: Exception) {
            Timber.e(e, "Error getting car ID for VIN: $vin")
            null
        }
    }

    private suspend fun createPdi(
        state: CheckScreenState,
        carId: Int,
        userId: Long
    ): Int? = withContext(Dispatchers.IO) {
        try {
            val pdi = PDI(
                pdi_id = null,
                car_id = carId,
                create_by_user_id = userId.toInt(),
                created_date = formatDateTime(LocalDateTime.now()),
                soc_percentage = state.socPercentage.toDoubleOrNull() ?: 0.0,
                battery12v_Voltage = state.batteryVoltage.toDoubleOrNull() ?: 0.0,
                tire_pressure_dd = state.frontRightPressure.toDoubleOrNull() ?: 0.0,
                tire_pressure_de = state.frontLeftPressure.toDoubleOrNull() ?: 0.0,
                tire_pressure_td = state.rearRightPressure.toDoubleOrNull() ?: 0.0,
                tire_pressure_te = state.rearLeftPressure.toDoubleOrNull() ?: 0.0,
                five_minutes_hybrid_check = state.isCarStarted,
                user_comments = state.additionalInfo
            )

            val response = RetrofitClient.pdiApi.postPdi(pdi)
            if (response.isSuccessful) {
                response.body()?.pdi_id
            } else {
                val errorBody = response.errorBody()?.string()
                Timber.e("Error creating PDI: ${response.code()} $errorBody")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error creating PDI")
            null
        }
    }

    private suspend fun uploadImagesForType(
        context: Context,
        pdiId: Int,
        imageUris: List<Uri>,
        imageType: String
    ) {
        if (imageUris.isEmpty()) return

        try {
            imageRepository.uploadImages(context, imageUris, pdiId, imageType)
        } catch (e: Exception) {
            Timber.e(e, "Error uploading $imageType images")
            throw e
        }
    }

    private fun formatDateTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        return dateTime.format(formatter)
    }

    companion object {
        // Singleton instance
        @Volatile
        private var INSTANCE: CheckScreenRepository? = null

        fun getInstance(): CheckScreenRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = CheckScreenRepository()
                INSTANCE = instance
                instance
            }
        }
    }
}