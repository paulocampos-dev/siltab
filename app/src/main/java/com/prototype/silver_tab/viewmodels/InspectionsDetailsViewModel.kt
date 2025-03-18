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

    fun submitVinCorrection(inspectionInfo: InspectionInfo, newVin: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                logTimber(tag, "Submitting VIN correction: ${inspectionInfo.vin} -> $newVin")

                // Check if the vehicle exists with this VIN
                inspectionInfo.carId?.let { carId ->
                    // In a real implementation, this would call an API endpoint
                    // For now, we'll simulate the correction process

                    // First, check if the new VIN already exists in the system
                    val existingCar = carRepository.getCarByVin(newVin)

                    if (existingCar != null) {
                        // VIN already exists in the system
                        _error.value = "A vehicle with this VIN already exists in the system"
                    } else {
                        // In a production app, you would call a specific API endpoint here
                        // For the prototype, we'll just log that it would be reported
                        logTimber(tag, "VIN correction would be reported to administrators")

                        // The backend would typically:
                        // 1. Create a correction record
                        // 2. Flag the record for review
                        // 3. Possibly send notifications to admins

//                        _success.value = "VIN correction submitted"
                    }
                } ?: run {
                    _error.value = "Cannot correct VIN: No car ID found"
                }
            } catch (e: Exception) {
                logTimberError(tag, "Error submitting VIN correction: ${e.message}")
                _error.value = "Failed to submit VIN correction: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * Mark a car as sold
     */
    fun markCarAsSold(inspectionInfo: InspectionInfo) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                inspectionInfo.vin?.let { vin ->
                    val result = carRepository.markCarAsSold(vin, mapOf("soldDate" to System.currentTimeMillis().toString()))
                    if (result.isSuccess) {
                        logTimber(tag, "Car marked as sold successfully: $vin")
                    } else {
                        val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                        logTimberError(tag, "Error marking car as sold: $errorMsg")
                        _error.value = "Failed to mark car as sold: $errorMsg"
                    }
                } ?: run {
                    _error.value = "No VIN available for this car"
                }
            } catch (e: Exception) {
                logTimberError(tag, "Exception marking car as sold: ${e.message}")
                _error.value = "Error marking car as sold: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Report wrong information for a vehicle
     */
    fun reportWrongInfo(inspectionInfo: InspectionInfo) {
        // In a real implementation, this would send a report to the backend
        // For now, we'll just log it
        logTimber(tag, "Reporting wrong information for VIN: ${inspectionInfo.vin}")
    }

    // Better simulation with more realistic image data
    private fun simulateImageLoad(pdiId: Int): List<ImageDTO> {
        // In a real app, this would be replaced with an API call

        // Create a list of images with actual file paths to sample images in your app
        val imageList = mutableListOf<ImageDTO>()

        // Add sample images for each type
        // For testing, we can use drawable resources by creating content URIs

        // Add VIN images
        imageList.add(
            ImageDTO(
                imageId = 1,
                pdiId = pdiId,
                imageTypeName = "vin",
                filePath = "android.resource://com.prototype.silver_tab/drawable/chassi"
            )
        )

        // Add SOC images
        imageList.add(
            ImageDTO(
                imageId = 2,
                pdiId = pdiId,
                imageTypeName = "soc",
                filePath = "android.resource://com.prototype.silver_tab/drawable/soc_example"
            )
        )

        // Add battery images
        imageList.add(
            ImageDTO(
                imageId = 3,
                pdiId = pdiId,
                imageTypeName = "battery12V",
                filePath = "android.resource://com.prototype.silver_tab/drawable/batteryhelpimage"
            )
        )

        // Add tire images
        imageList.add(
            ImageDTO(
                imageId = 4,
                pdiId = pdiId,
                imageTypeName = "tire",
                filePath = "android.resource://com.prototype.silver_tab/drawable/pneus"
            )
        )

        return imageList
    }
}