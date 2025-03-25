package com.prototype.silver_tab.data.repository

import com.prototype.silver_tab.data.models.car.Car
import com.prototype.silver_tab.data.models.car.toCar
import com.prototype.silver_tab.data.models.pdi.InspectionState
import com.prototype.silver_tab.data.routes.PdiRoutes
import com.prototype.silver_tab.data.models.pdi.PDI
import com.prototype.silver_tab.data.models.pdi.PdiRequest
import com.prototype.silver_tab.utils.isNoPdiRecordsError
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InspectionRepository @Inject constructor(
    private val pdiRoutes: PdiRoutes
) {
    private val tag = "InspectionRepository"

    private val _inspectionState = MutableStateFlow<InspectionState>(InspectionState.Loading)
    val inspectionState: StateFlow<InspectionState> = _inspectionState.asStateFlow()

    // Cache for dealer inspections
    private var inspectionsCache: Map<String, List<PDI>> = emptyMap()

    suspend fun getDealerInspections(dealerCode: String, forceRefresh: Boolean = false): List<PDI> {
        logTimber(tag, "Getting dealer inspections")

        if (!forceRefresh && inspectionsCache.containsKey(dealerCode)) {
            Timber.d("Returning cached inspections for dealer: $dealerCode")
            return inspectionsCache[dealerCode] ?: emptyList()
        }

        _inspectionState.value = InspectionState.Loading

        try {
            val response = pdiRoutes.getPdisByDealer(dealerCode)

            if (response.isSuccessful) {
                val inspections = response.body() ?: emptyList()
                inspectionsCache = inspectionsCache + (dealerCode to inspections)
                _inspectionState.value = InspectionState.Success(inspections)
                logTimber(tag, "Successfully fetched ${inspections.size} inspections for dealer: $dealerCode")

                return inspections
            } else {

                if (isNoPdiRecordsError(response)) {
                    logTimber(tag, "No PDI records found for dealer: $dealerCode")
                    inspectionsCache = inspectionsCache + (dealerCode to emptyList())
                    _inspectionState.value = InspectionState.Success(emptyList())
                    return emptyList()
                } else {
                    val errorMsg = "Error fetching inspections: ${response.code()} - ${response.message()}"

                    logTimberError(tag, errorMsg)
                    _inspectionState.value = InspectionState.Error(errorMsg)
                    return emptyList()
                }
            }
        } catch (e: Exception) {
            val errorMsg = "Exception fetching inspections: ${e.message}"
            logTimberError(tag, errorMsg)
            _inspectionState.value = InspectionState.Error(errorMsg)
            return emptyList()
        }
    }

    suspend fun createInspection(pdiRequest: PdiRequest): Result<PDI> {
        try {
            val response = pdiRoutes.createPdi(pdiRequest)

            if (response.isSuccessful) {
                val createdPdi = response.body()
                if (createdPdi != null) {
                    // Clear all caches because a new PDI affects what's shown in history
                    inspectionsCache = emptyMap()
                    Timber.d("Successfully created PDI for car ID: ${pdiRequest.carId}")
                    return Result.success(createdPdi)
                } else {
                    return Result.failure(Exception("Created PDI response was null"))
                }
            } else {
                val errorMsg = "Error creating PDI: ${response.code()} - ${response.message()}"
                Timber.e(errorMsg)
                return Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception creating PDI: ${e.message}")
            return Result.failure(e)
        }
    }

    suspend fun updateInspection(pdiId: Int, pdiRequest: PdiRequest): Result<PDI> {
        try {
            val updatedRequest = pdiRequest.copy(pdiId = pdiId)

            val response = pdiRoutes.updatePdi(pdiId, updatedRequest)

            if (response.isSuccessful) {
                val updatedPdi = response.body()
                if (updatedPdi != null) {
                    // Clear all caches
                    inspectionsCache = emptyMap()
                    Timber.d("Successfully updated PDI with ID: $pdiId")
                    return Result.success(updatedPdi)
                } else {
                    return Result.failure(Exception("Updated PDI response was null"))
                }
            } else {
                val errorMsg = "Error updating PDI: ${response.code()} - ${response.message()}"
                Timber.e(errorMsg)
                return Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception updating PDI: ${e.message}")
            return Result.failure(e)
        }
    }
    suspend fun changePendingStatus(pdiId: Int?, newSoc: Float): Result<PDI> {
        try {
            logTimber(tag, "Changing pending of pdi id $pdiId, new soc: $newSoc")

            // Create request payload as a Map
            val newSocMap = mapOf("new_soc" to newSoc)

            // Make the API call
            val response = pdiRoutes.changePendingStatus(pdiId?:0, newSocMap)

            return if (response.isSuccessful) {
                val pendingResponse = response.body()
                if (pendingResponse != null) {
                    // Convert to Car model
                    val pdi = pendingResponse

                    // Clear all caches as the sold status has changed
                    clearCache()
                    logTimber(tag, "Successfully updated pdi pending status: $pdiId with response: $pendingResponse")
                    Result.success(pdi)
                } else {
                    // Add extra logging to see error details
                    logTimberError(tag, "updated pdi pending status null")
                    try {
                        val responseString = response.errorBody()?.string()
                        logTimberError(tag, "Response body: $responseString")
                    } catch (e: Exception) {
                        // Ignore
                    }

                    Result.failure(Exception("updating pdi pending status failed"))
                }
            } else {
                val errorMsg = "Error updating pdi pending status: ${response.code()} - ${response.message()}"
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
            logTimberError(tag, "Exception updating pdi pending status: ${e.message}")
            e.printStackTrace()
            return Result.failure(e)
        }
    }


    fun clearCache(dealerCode: String? = null) {
        if (dealerCode != null) {
            inspectionsCache = inspectionsCache - dealerCode
            Timber.d("Cleared inspection cache for dealer: $dealerCode")
        } else {
            inspectionsCache = emptyMap()
            Timber.d("Cleared all inspection caches")
        }
    }
}