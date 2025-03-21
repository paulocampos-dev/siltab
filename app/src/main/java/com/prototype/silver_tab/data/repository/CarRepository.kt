package com.prototype.silver_tab.data.repository

import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.car.Car
import com.prototype.silver_tab.data.models.car.CarResponse
import com.prototype.silver_tab.data.models.car.CarState
import com.prototype.silver_tab.data.models.car.UpdateVinResponse
import com.prototype.silver_tab.data.models.car.VinUpdateRequest
import com.prototype.silver_tab.data.models.car.toCar
import com.prototype.silver_tab.data.routes.CarRoutes
import com.prototype.silver_tab.utils.convertResponseToCar
import com.prototype.silver_tab.utils.convertResponsesToCars
import com.prototype.silver_tab.utils.isNoCarsFoundError
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarRepository @Inject constructor(
    private val carRoutes: CarRoutes
) {
    val tag = "CarRepository"
    private val _carState = MutableStateFlow<CarState>(CarState.Loading)
    val carState: StateFlow<CarState> = _carState.asStateFlow()

    // Cache for car data
    private var carsCache: Map<String, List<Car>> = emptyMap()

    suspend fun getDealerCars(dealerCode: String, forceRefresh: Boolean = false): List<Car> {

        if (!forceRefresh && carsCache.containsKey(dealerCode)) {
            logTimber(tag, "Returning cached cars for dealer: $dealerCode")
            _carState.value = CarState.Success(carsCache[dealerCode] ?: emptyList())
            return carsCache[dealerCode] ?: emptyList()
        }

        _carState.value = CarState.Loading

        try {
            val response = carRoutes.getCarByDealerCode(dealerCode)

            if (response.isSuccessful) {
                val carResponse = response.body() ?: emptyList()
                val cars = convertResponsesToCars(carResponse)
                carsCache = carsCache + (dealerCode to cars)
                _carState.value = CarState.Success(cars)
                logTimber(tag, "Successfully fetched ${cars.size} cars for dealer: $dealerCode")

                return cars
            } else {
                // Check if this is the "No cars found" error wrapped in a 500 or other error response
                if (isNoCarsFoundError(response)) {
                    // Treat as success with empty list
                    logTimber(tag, "No cars found for dealer: $dealerCode - treating as empty list")
                    carsCache = carsCache + (dealerCode to emptyList())
                    _carState.value = CarState.Success(emptyList())
                    return emptyList()
                } else {
                    // This is a genuine error
                    val errorMsg = "Error fetching cars: ${response.code()} - ${response.message()}"
                    logTimberError(tag, errorMsg)
                    _carState.value = CarState.Error(errorMsg)
                    return emptyList()
                }
            }
        } catch (e: Exception) {
            val errorMsg = "Exception fetching cars: ${e.message}"
            logTimberError(tag, errorMsg)
            _carState.value = CarState.Error(errorMsg)
            return emptyList()
        }
    }

    suspend fun updateCarVin(carId: Int, newVin: String): Result<UpdateVinResponse> {
        try {
            logTimber(tag, "Updating VIN for car ID: $carId, new VIN: $newVin")

            // Create a request with carId as a STRING
            val request = VinUpdateRequest(
                carId = carId,
                vin = newVin
            )

            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(VinUpdateRequest::class.java)
            val jsonPayload = adapter.toJson(request)
            logTimber(tag, "Raw JSON payload: $jsonPayload")

            logTimber(tag, "Sending request: $request")

            // Make the API call
            val response = carRoutes.changeWrongVin(carId, request)

            if (response.isSuccessful) {
                val updatedCar = response.body()
                logTimber(tag, "Updated car response: $updatedCar")
                if (updatedCar != null) {
                    // Clear all caches as the VIN has changed
                    clearCache()
                    logTimber(tag, "Successfully updated VIN for car ID: $carId to: $newVin")
                    return Result.success(updatedCar)
                } else {
                    logTimberError(tag, "Updated car response was null")
                    return Result.failure(Exception("Updated car response was null"))
                }
            } else {
                val errorMsg = "Error updating car VIN: ${response.code()} - ${response.message()}"
                logTimberError(tag, errorMsg)
                try {
                    val errorBody = response.errorBody()?.string()
                    logTimberError(tag, "Error response body: $errorBody")
                } catch (e: Exception) {
                    // Ignore if we can't read the error body
                }
                return Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            logTimberError(tag, "Exception updating car VIN: ${e.message}")
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    suspend fun getCarByVin(vin: String): Car? {
        logTimber(tag, "Looking up car by VIN: $vin")
        try {
            val response = carRoutes.getCarByVin(vin)

            if (response.isSuccessful) {
                val carResponse = response.body()
                if (carResponse != null) {
                    logTimber(tag, "Found car with VIN: $vin, dealer: ${carResponse.dealerCode}")
                    return convertResponseToCar(carResponse)
                } else {
                    logTimber(tag, "No car found with VIN: $vin (empty response body)")
                    return null
                }
            } else {
                // Check if this is a "not found" response (404)
                if (response.code() == 404) {
                    logTimber(tag, "No car found with VIN: $vin (404 response)")
                    return null
                } else {
                    logTimberError(tag, "Error fetching car by VIN: ${response.code()} - ${response.message()}")
                    return null
                }
            }
        } catch (e: Exception) {
            logTimberError(tag, "Exception fetching car by VIN: ${e.message}")
            return null
        }
    }

    suspend fun getCarIdByVin(vin: String): Int? {
        try {
            logTimber(tag, "Looking up car ID for VIN: $vin")

            val car = getCarByVin(vin)
            return car?.carId
        } catch (e: Exception) {
            logTimberError(tag, "Error getting car ID for VIN: ${e.message}")
            return null
        }
    }

    suspend fun createCar(car: CarResponse): Result<CarResponse> {
        try {
            val response = carRoutes.postCar(car)

            return if (response.isSuccessful) {
                val createdCar = response.body()
                if (createdCar != null) {
                    // Clear cache for the dealer to force refresh
                    carsCache = carsCache - car.dealerCode
                    Timber.d("Successfully created car with VIN: ${car.vin}")
                    Result.success(createdCar)
                } else {
                    Result.failure(Exception("Created car response was null"))
                }
            } else {
                val errorMsg = "Error creating car: ${response.code()} - ${response.message()}"
                logTimberError(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            logTimberError(tag, "Exception creating car: ${e.message}")
            return Result.failure(e)
        }
    }

    suspend fun markCarAsSold(vin: String, soldDate: String? = null): Result<Car> {
        try {
            logTimber(tag, "Marking car with VIN $vin as sold, date: $soldDate")

            // Use provided date or current datetime string in ISO format
            val dateToUse = soldDate ?: java.time.OffsetDateTime.now().toString()

            // Create request payload as a Map
            val soldData = mapOf("sold_date" to dateToUse)

            // Make the API call
            val response = carRoutes.markCarAsSold(vin, soldData)

            return if (response.isSuccessful) {
                val soldCarResponse = response.body()
                if (soldCarResponse != null) {
                    // Convert to Car model
                    val car = soldCarResponse.toCar()

                    // Clear all caches as the sold status has changed
                    clearCache()
                    logTimber(tag, "Successfully marked car as sold: $vin with response: $soldCarResponse")
                    Result.success(car)
                } else {
                    // Add extra logging to see error details
                    logTimberError(tag, "Marked car as sold response was null")
                    try {
                        val responseString = response.errorBody()?.string()
                        logTimberError(tag, "Response body: $responseString")
                    } catch (e: Exception) {
                        // Ignore
                    }

                    Result.failure(Exception("Marked car as sold response was null"))
                }
            } else {
                val errorMsg = "Error marking car as sold: ${response.code()} - ${response.message()}"
                logTimberError(tag, errorMsg)
                try {
                    val errorBody = response.errorBody()?.string()
                    logTimberError(tag, "Error response body: $errorBody")
                } catch (e: Exception) {
                    // Ignore if we can't read the error body
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            logTimberError(tag, "Exception marking car as sold: ${e.message}")
            e.printStackTrace()
            return Result.failure(e)
        }
    }


    /**
     * Clears the car cache for a specific dealer or all dealers
     * @param dealerCode Optional dealer code to clear cache for. If null, clears all caches.
     */
    fun clearCache(dealerCode: String? = null) {
        if (dealerCode != null) {
            carsCache = carsCache - dealerCode
            Timber.d("Cleared car cache for dealer: $dealerCode")
        } else {
            carsCache = emptyMap()
            Timber.d("Cleared all car caches")
        }
    }
}