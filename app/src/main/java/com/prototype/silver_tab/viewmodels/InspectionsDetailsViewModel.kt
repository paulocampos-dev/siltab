package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.models.ImageDTO
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.repository.CarRepository
import com.prototype.silver_tab.data.repository.ImageRepository
import com.prototype.silver_tab.data.repository.InspectionRepository
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InspectionDetailsViewModel @Inject constructor(
    private val carRepository: CarRepository,
    private val inspectionRepository: InspectionRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val tag = "InspectionDetailsViewModel"

    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Images state
    private val _pdiImages = MutableStateFlow<List<ImageDTO>>(emptyList())
    val pdiImages: StateFlow<List<ImageDTO>> = _pdiImages.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    fun showVinCorrectionDialog(inspectionInfo: InspectionInfo) {
        // Reset any previous errors or success messages
        _error.value = null
        _success.value = null

        // Ensure we have a VIN to correct
        if (inspectionInfo.vin.isNullOrBlank()) {
            _error.value = "No VIN available to correct"
            return
        }

        // The dialog will be shown by the UI based on a state value
        // that you'll need to add to the InspectionDetailsDialog composable
    }

    // Also add this helper function to clear the success message after it's been shown
    fun clearSuccessMessage() {
        _success.value = null
    }

    // Add this helper function to clear the error message
    fun clearErrorMessage() {
        _error.value = null
    }

    fun resetStates() {
        // Reset all state flows that could affect UI
        _error.value = null
        _success.value = null
        _isLoading.value = false
    }

    suspend fun loadPdiImages(pdiId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Use the repository to fetch images
                val fetchedImages = imageRepository.getAllPdiImages(pdiId)
                _pdiImages.value = fetchedImages
                logTimber(tag, "Loaded ${fetchedImages.size} images for PDI: $pdiId")

                // Add this debug line to see what types are coming from the API
                fetchedImages.forEach { image ->
                    logTimber(tag, "Image ID: ${image.imageId}, Type: ${image.imageTypeName}")
                }
            } catch (e: Exception) {
                logTimberError(tag, "Error loading images: ${e.message}")
                _error.value = "Failed to load images: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun loadInspectionDetails(inspectionInfo: InspectionInfo) {
        logTimber(tag, "Loading details for inspection: ${inspectionInfo.vin}, PDI ID: ${inspectionInfo.pdiId}, Car ID: ${inspectionInfo.carId}")
        // Start loading images immediately if there's a PDI ID
        inspectionInfo.pdiId?.let { pdiId ->
            loadPdiImages(pdiId)
        }

        // We could also load other details here in the future
    }


    fun getImagesOfType(type: String): List<ImageDTO> {
        val allImages = _pdiImages.value
        val typeImages = allImages.filter {
            it.imageTypeName?.equals(type, ignoreCase = true) == true
        }
        logTimber(tag, "getImagesOfType: $type - All images: ${allImages.size}, Filtered images: ${typeImages.size}")
        return typeImages
    }


    fun markCarAsSold(inspectionInfo: InspectionInfo, soldDate: String? = null) {
        viewModelScope.launch {
            // Always start with clean state
            _error.value = null
            _success.value = null
            _isLoading.value = true

            try {
                inspectionInfo.vin?.let { vin ->
                    logTimber(tag, "Marking car as sold: $vin with date: $soldDate")

                    val result = carRepository.markCarAsSold(vin, soldDate)

                    if (result.isSuccess) {
                        logTimber(tag, "Car marked as sold successfully: $vin")

                        // Set success message
                        _success.value = "Vehicle successfully marked as sold"

                        // Make sure we wait a bit before clearing loading state
                        delay(100)
                        _isLoading.value = false
                    } else {
                        val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                        logTimberError(tag, "Error marking car as sold: $errorMsg")
                        _error.value = "Failed to mark car as sold: $errorMsg"
                        _isLoading.value = false
                    }
                } ?: run {
                    _error.value = "No VIN available for this car"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                logTimberError(tag, "Exception marking car as sold: ${e.message}")
                _error.value = "Error marking car as sold: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun submitVinCorrection(originalVin: String, newVin: String) {
        viewModelScope.launch {
            // Always start with clean state
            _error.value = null
            _success.value = null
            _isLoading.value = true

            try {
                logTimber(tag, "Submitting VIN correction: $originalVin -> $newVin")

                // First get the car ID for the original VIN
                val carId = carRepository.getCarIdByVin(originalVin)

                if (carId != null) {
                    logTimber(tag, "Found car ID: $carId for VIN: $originalVin")

                    // Check if new VIN already exists
                    val existingCarWithNewVin = carRepository.getCarByVin(newVin)
                    if (existingCarWithNewVin != null) {
                        logTimberError(tag, "VIN $newVin already exists in the system")
                        _error.value = "A vehicle with this VIN already exists in the system"
                        _isLoading.value = false
                        return@launch
                    }

                    // Update the VIN with proper string conversion to match interface
                    val payload = mapOf(
                        "carId" to carId.toString(),
                        "vin" to newVin
                    )

                    val result = carRepository.updateCarVin(carId, newVin)

                    if (result.isSuccess) {
                        logTimber(tag, "VIN correction successful")
                        _error.value = null // Clear any lingering errors
                        _success.value = "VIN correction successful"
                        _isLoading.value = false
                    } else {
                        val errorMsg = "Failed to update VIN: ${result.exceptionOrNull()?.message}"
                        logTimberError(tag, errorMsg)
                        _success.value = null // Clear any success messages
                        _error.value = errorMsg
                        _isLoading.value = false
                    }
                } else {
                    logTimberError(tag, "Could not find car ID for VIN: $originalVin")
                    _success.value = null
                    _error.value = "Could not find car for VIN: $originalVin"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                logTimberError(tag, "Error submitting VIN correction: ${e.message}")
                _success.value = null
                _error.value = "Error submitting VIN correction: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun submitPendingUpdate(pdiId: Int?, newSoc: Float) {
        viewModelScope.launch {
            // Always start with clean state
            _error.value = null
            _success.value = null
            _isLoading.value = true

            try {
                logTimber(tag, "Submitting Pending update: $newSoc")
                    val result = inspectionRepository.changePendingStatus(pdiId, newSoc)

                    if (result.isSuccess) {
                        logTimber(tag, "Pending Status update successful")
                        _error.value = null // Clear any lingering errors
                        _success.value = "Pending Status update successful"
                        _isLoading.value = false
                    } else {
                        val errorMsg = "Failed to update VIN: ${result.exceptionOrNull()?.message}"
                        logTimberError(tag, errorMsg)
                        _success.value = null // Clear any success messages
                        _error.value = errorMsg
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                logTimberError(tag, "Error submitting VIN correction: ${e.message}")
                _success.value = null
                _error.value = "Error submitting VIN correction: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}