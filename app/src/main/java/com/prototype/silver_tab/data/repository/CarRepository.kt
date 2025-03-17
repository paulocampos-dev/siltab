package com.prototype.silver_tab.data.repository

import com.prototype.silver_tab.data.models.car.Car
import com.prototype.silver_tab.data.models.car.CarResponse
import com.prototype.silver_tab.data.models.car.CarState
import com.prototype.silver_tab.data.routes.CarRoutes
import com.prototype.silver_tab.utils.convertResponseToCar
import com.prototype.silver_tab.utils.convertResponsesToCars
import com.prototype.silver_tab.utils.isNoCarsFoundError
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError
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

    suspend fun getCarByVin(vin: String): Car? {
        try {
            val response = carRoutes.getCarByVin(vin)

            return if (response.isSuccessful) {
                response.body()?.let { convertResponseToCar(it) }
            } else {
                Timber.e("Error fetching car by VIN: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception fetching car by VIN: ${e.message}")
            return null
        }
    }

    /**
     * Checks if a VIN exists for a specific dealer
     * @param vin The VIN to check
     * @param dealerCode The dealer code to check against
     * @return true if the VIN exists, false otherwise
     */
    suspend fun checkVinExists(vin: String, dealerCode: String): Boolean {
        // Try to get existing cars from cache first
        val dealerCars = if (carsCache.containsKey(dealerCode)) {
            carsCache[dealerCode] ?: emptyList()
        } else {
            getDealerCars(dealerCode)
        }

        return dealerCars.any { it.vin == vin }
    }

    /**
     * Creates a new car
     * @param car The car data to create
     * @return Result with the created car if successful, an error otherwise
     */
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
                Timber.e(errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception creating car: ${e.message}")
            return Result.failure(e)
        }
    }

    /**
     * Marks a car as sold
     * @param vin The VIN of the car to mark as sold
     * @param soldData Additional data for the sold car
     * @return Result with the updated car if successful, an error otherwise
     */
    suspend fun markCarAsSold(vin: String, soldData: Map<String, String>): Result<Car> {
        try {
            val response = carRoutes.markCarAsSold(vin, soldData)

            return if (response.isSuccessful) {
                val updatedCar = response.body()
                if (updatedCar != null) {
                    // Clear all caches as we don't know which dealer this car belongs to
                    carsCache = emptyMap()
                    Timber.d("Successfully marked car as sold: $vin")
                    Result.success(updatedCar)
                } else {
                    Result.failure(Exception("Updated car response was null"))
                }
            } else {
                val errorMsg = "Error marking car as sold: ${response.code()} - ${response.message()}"
                Timber.e(errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception marking car as sold: ${e.message}")
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