package com.prototype.silver_tab.viewmodels

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.models.ImageDTO
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.car.CarResponse
import com.prototype.silver_tab.data.models.pdi.PDI
import com.prototype.silver_tab.data.models.pdi.PdiRequest
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.data.repository.CarRepository
import com.prototype.silver_tab.data.repository.InspectionRepository
import com.prototype.silver_tab.session.AppSessionManager
import com.prototype.silver_tab.utils.getModelIdFromName
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.validateNumericInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CheckScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val appSessionManager: AppSessionManager,
    private val carRepository: CarRepository,
    private val inspectionRepository: InspectionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val tag = "CheckScreenViewModel"

    // Form fields
    private val _vin = MutableStateFlow("")
    val vin: StateFlow<String> = _vin.asStateFlow()

    private val _socPercentage = MutableStateFlow("")
    val socPercentage: StateFlow<String> = _socPercentage.asStateFlow()

    private val _battery12vVoltage = MutableStateFlow("")
    val battery12vVoltage: StateFlow<String> = _battery12vVoltage.asStateFlow()

    private val _fiveMinutesHybridCheck = MutableStateFlow(false)
    val fiveMinutesHybridCheck: StateFlow<Boolean> = _fiveMinutesHybridCheck.asStateFlow()

    private val _tirePressureFrontRight = MutableStateFlow("")
    val tirePressureFrontRight: StateFlow<String> = _tirePressureFrontRight.asStateFlow()

    private val _tirePressureFrontLeft = MutableStateFlow("")
    val tirePressureFrontLeft: StateFlow<String> = _tirePressureFrontLeft.asStateFlow()

    private val _tirePressureRearRight = MutableStateFlow("")
    val tirePressureRearRight: StateFlow<String> = _tirePressureRearRight.asStateFlow()

    private val _tirePressureRearLeft = MutableStateFlow("")
    val tirePressureRearLeft: StateFlow<String> = _tirePressureRearLeft.asStateFlow()

    private val _comments = MutableStateFlow("")
    val comments: StateFlow<String> = _comments.asStateFlow()

    // Images for each section
    private val _vinImages = MutableStateFlow<List<ImageDTO>>(emptyList())
    val vinImages: StateFlow<List<ImageDTO>> = _vinImages.asStateFlow()

    private val _socImages = MutableStateFlow<List<ImageDTO>>(emptyList())
    val socImages: StateFlow<List<ImageDTO>> = _socImages.asStateFlow()

    private val _batteryImages = MutableStateFlow<List<ImageDTO>>(emptyList())
    val batteryImages: StateFlow<List<ImageDTO>> = _batteryImages.asStateFlow()

    private val _tireImages = MutableStateFlow<List<ImageDTO>>(emptyList())
    val tireImages: StateFlow<List<ImageDTO>> = _tireImages.asStateFlow()

    private val _extraImages = MutableStateFlow<List<ImageDTO>>(emptyList())
    val extraImages: StateFlow<List<ImageDTO>> = _extraImages.asStateFlow()

    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    // Car info
    private val _selectedCar = MutableStateFlow<InspectionInfo?>(null)
    val selectedCar: StateFlow<InspectionInfo?> = _selectedCar.asStateFlow()

    private val _isNewCar = MutableStateFlow(false)
    val isNewCar: StateFlow<Boolean> = _isNewCar.asStateFlow()

    private val _existingPdi = MutableStateFlow<PDI?>(null)
    val existingPdi: StateFlow<PDI?> = _existingPdi.asStateFlow()

    // Validation fields
    private val _vinError = MutableStateFlow<String?>(null)
    val vinError: StateFlow<String?> = _vinError.asStateFlow()

    private val _socError = MutableStateFlow<String?>(null)
    val socError: StateFlow<String?> = _socError.asStateFlow()

    private val _batteryError = MutableStateFlow<String?>(null)
    val batteryError: StateFlow<String?> = _batteryError.asStateFlow()

    private val _tirePressureErrors = MutableStateFlow(mapOf<String, String>())
    val tirePressureErrors: StateFlow<Map<String, String>> = _tirePressureErrors.asStateFlow()

    // Feature flags
    private val _needsBattery12vSection = MutableStateFlow(false)
    val needsBattery12vSection: StateFlow<Boolean> = _needsBattery12vSection.asStateFlow()

    private val _needsHybridCheckSection = MutableStateFlow(false)
    val needsHybridCheckSection: StateFlow<Boolean> = _needsHybridCheckSection.asStateFlow()

    init {
        // Get the carChassi and isNew parameters from route
        val carChassi = savedStateHandle.get<String>("carChassi") ?: "new"
        val isNew = savedStateHandle.get<Boolean>("isNew") ?: false

        logTimber(tag, "Initializing with carChassi: $carChassi, isNew: $isNew")

        _isNewCar.value = isNew

        // Load session data
        viewModelScope.launch {
            val sessionCar = appSessionManager.selectedInspection.first()
            sessionCar?.let {
                _selectedCar.value = it
                logTimber(tag, "Loaded car from session: ${it.name}")

                // Set feature flags based on car model
                setupFeatureFlags(it.name)

                if (!isNew && carChassi != "new") {
                    // Load existing PDI if updating
                    loadExistingPdi(carChassi)
                }

                // For existing car but new PDI, prefill VIN if available
                if (isNew && carChassi != "new") {
                    _vin.value = carChassi
                }
            }
        }
    }

    private fun setupFeatureFlags(carName: String) {
        // Check if the model needs 12V battery section
        _needsBattery12vSection.value = carName.contains("DOLPHIN MINI", ignoreCase = true) ||
                carName.contains("YUAN PRO", ignoreCase = true)

        // Check if the model is hybrid and needs the hybrid check section
        _needsHybridCheckSection.value = carName.contains("HYBRID", ignoreCase = true) ||
                carName.contains("DM-I", ignoreCase = true) ||
                carName.contains("TAN", ignoreCase = true) ||
                carName.contains("SONG", ignoreCase = true) ||
                carName.contains("KING", ignoreCase = true) ||
                carName.contains("SHARK", ignoreCase = true)
    }

    private suspend fun loadExistingPdi(vinOrId: String) {
        _isLoading.value = true

        try {
            // Implementation for loading existing PDI details will go here
            // This will involve fetching the PDI from the repository

            // For now, we'll simulate with dummy data
            _isLoading.value = false
        } catch (e: Exception) {
            _error.value = "Failed to load PDI: ${e.message}"
            _isLoading.value = false
        }
    }

    // Update functions for form fields
    fun updateVin(vin: String) {
        _vin.value = vin
        validateVin(vin)
    }

    fun updateSocPercentage(soc: String) {
        _socPercentage.value = validateNumericInput(soc)
        validateSoc(soc)
    }

    fun updateBattery12vVoltage(voltage: String) {
        _battery12vVoltage.value = validateNumericInput(voltage)
        validateBattery(voltage)
    }

    fun updateFiveMinutesHybridCheck(checked: Boolean) {
        _fiveMinutesHybridCheck.value = checked
    }

    fun updateTirePressureFrontRight(pressure: String) {
        _tirePressureFrontRight.value = validateNumericInput(pressure)
        validateTirePressure("frontRight", pressure)
    }

    fun updateTirePressureFrontLeft(pressure: String) {
        _tirePressureFrontLeft.value = validateNumericInput(pressure)
        validateTirePressure("frontLeft", pressure)
    }

    fun updateTirePressureRearRight(pressure: String) {
        _tirePressureRearRight.value = validateNumericInput(pressure)
        validateTirePressure("rearRight", pressure)
    }

    fun updateTirePressureRearLeft(pressure: String) {
        _tirePressureRearLeft.value = validateNumericInput(pressure)
        validateTirePressure("rearLeft", pressure)
    }

    fun updateComments(comment: String) {
        _comments.value = comment
    }

    // Validations
    private fun validateVin(vin: String) {
        when {
            vin.isBlank() -> _vinError.value = "VIN cannot be empty"
            // TODO: Validate VIN properly when launching
//            vin.length != 17 -> _vinError.value = "VIN must be 17 characters long"
//            !vin.matches(Regex("[A-HJ-NPR-Z0-9]{17}")) -> _vinError.value = "Invalid VIN format"
            else -> _vinError.value = null
        }
    }

    private fun validateSoc(soc: String) {
        if (soc.isBlank()) {
            _socError.value = null
            return
        }

        try {
            val socValue = soc.toDouble()
            if (socValue < 0 || socValue > 100) {
                _socError.value = "SOC must be between 0 and 100%"
            } else {
                _socError.value = null
            }
        } catch (e: NumberFormatException) {
            _socError.value = "SOC must be a valid number"
        }
    }

    private fun validateBattery(voltage: String) {
        if (voltage.isBlank()) {
            _batteryError.value = null
            return
        }

        try {
            val voltageValue = voltage.toDouble()
            if (voltageValue <= 0 || voltageValue > 15) {
                _batteryError.value = "Battery voltage must be between 0 and 15V"
            } else {
                _batteryError.value = null
            }
        } catch (e: NumberFormatException) {
            _batteryError.value = "Battery voltage must be a valid number"
        }
    }

    private fun validateTirePressure(tire: String, pressure: String) {
        if (pressure.isBlank()) {
            _tirePressureErrors.value = _tirePressureErrors.value - tire
            return
        }

        try {
            val pressureValue = pressure.toDouble()
            if (pressureValue <= 0 || pressureValue > 50) {
                _tirePressureErrors.value = _tirePressureErrors.value + (tire to "Pressure must be between 0 and 50 PSI")
            } else {
                _tirePressureErrors.value = _tirePressureErrors.value - tire
            }
        } catch (e: NumberFormatException) {
            _tirePressureErrors.value = _tirePressureErrors.value + (tire to "Pressure must be a valid number")
        }
    }

    fun validateAllFields(): Boolean {
        validateVin(_vin.value)
        validateSoc(_socPercentage.value)

        if (_needsBattery12vSection.value) {
            validateBattery(_battery12vVoltage.value)
        }

        validateTirePressure("frontRight", _tirePressureFrontRight.value)
        validateTirePressure("frontLeft", _tirePressureFrontLeft.value)
        validateTirePressure("rearRight", _tirePressureRearRight.value)
        validateTirePressure("rearLeft", _tirePressureRearLeft.value)

        return _vinError.value == null &&
                _socError.value == null &&
                (!_needsBattery12vSection.value || _batteryError.value == null) &&
                _tirePressureErrors.value.isEmpty()
    }

    // Image handling
    fun processAndAddImage(section: String, uri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // For now, we'll create a simple ImageDTO with the URI string
                val imageDTO = ImageDTO(
                    imageId = null,
                    pdiId = null,
                    pdiImageType = section,
                    imageData = null,
                    fileName = uri.lastPathSegment,
                    filePath = uri.toString()
                )

                // Add to appropriate section
                addImage(section, imageDTO)

            } catch (e: Exception) {
                _error.value = "Failed to process image: ${e.message}"
                logTimber(tag, "Error processing image: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun addImage(section: String, image: ImageDTO) {
        when (section) {
            "vin" -> {
                val current = _vinImages.value.toMutableList()
                if (current.size < 4) {
                    current.add(image)
                    _vinImages.value = current
                }
            }
            "soc" -> {
                val current = _socImages.value.toMutableList()
                if (current.size < 4) {
                    current.add(image)
                    _socImages.value = current
                }
            }
            "battery" -> {
                val current = _batteryImages.value.toMutableList()
                if (current.size < 4) {
                    current.add(image)
                    _batteryImages.value = current
                }
            }
            "tire" -> {
                val current = _tireImages.value.toMutableList()
                if (current.size < 4) {
                    current.add(image)
                    _tireImages.value = current
                }
            }
            "extra" -> {
                val current = _extraImages.value.toMutableList()
                if (current.size < 4) {
                    current.add(image)
                    _extraImages.value = current
                }
            }
        }
    }

    fun removeImage(section: String, imageIndex: Int) {
        when (section) {
            "vin" -> {
                val current = _vinImages.value.toMutableList()
                if (imageIndex in current.indices) {
                    current.removeAt(imageIndex)
                    _vinImages.value = current
                }
            }
            "soc" -> {
                val current = _socImages.value.toMutableList()
                if (imageIndex in current.indices) {
                    current.removeAt(imageIndex)
                    _socImages.value = current
                }
            }
            "battery" -> {
                val current = _batteryImages.value.toMutableList()
                if (imageIndex in current.indices) {
                    current.removeAt(imageIndex)
                    _batteryImages.value = current
                }
            }
            "tire" -> {
                val current = _tireImages.value.toMutableList()
                if (imageIndex in current.indices) {
                    current.removeAt(imageIndex)
                    _tireImages.value = current
                }
            }
            "extra" -> {
                val current = _extraImages.value.toMutableList()
                if (imageIndex in current.indices) {
                    current.removeAt(imageIndex)
                    _extraImages.value = current
                }
            }
        }
    }

    // Save PDI
    fun savePdi() {
        viewModelScope.launch {
            if (!validateAllFields()) {
                _error.value = "Please fix the errors before submitting"
                return@launch
            }

            _isLoading.value = true
            _error.value = null
            _success.value = null

            try {
                val selectedCar = _selectedCar.value ?: throw Exception("No car selected")

                // Get current user ID from auth state
                val userId = authRepository.authState.value.userId ?: throw Exception("User not authenticated")

                // Generate current timestamp in ISO format
                val currentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    .format(Date())

                // For new car, first create the car entry
                val carId = if (_isNewCar.value) {
                    val carModelId = getModelIdFromName(selectedCar.name) ?: throw Exception("Invalid car model")
//                    val dealerCode = selectedCar.dealerCode ?: throw Exception("No dealer code found")
                    val dealerCode = appSessionManager.selectedDealer.value?.dealerCode ?: throw Exception("No dealer found")

                    // Create car request
                    val carRequest = CarResponse(
                        carId = null,
                        carModelId = carModelId,
                        carModelName = selectedCar.name,
                        dealerCode = dealerCode,
                        vin = _vin.value,
                        isSold = false,
                        createdAt = null,
                        updatedAt = null
                    )

                    // Create car
                    val result = carRepository.createCar(carRequest)
                    if (result.isSuccess) {
                        result.getOrNull()?.carId ?: throw Exception("Car created but no ID returned")
                    } else {
                        throw Exception("Failed to create car: ${result.exceptionOrNull()?.message}")
                    }
                } else {
                    // For existing car, use the carId from the selectedCar
                    selectedCar.carId ?: throw Exception("Car ID not found")
                }

                // Now create the PDI request
                val pdiRequest = PdiRequest(
                    carId = carId,
                    createByUserId = userId,
                    createdDate = currentDate,
                    socPercentage = _socPercentage.value.toDoubleOrNull(),
                    battery12vVoltage = if (_needsBattery12vSection.value) _battery12vVoltage.value.toDoubleOrNull() else null,
                    fiveMinutesHybridCheck = if (_needsHybridCheckSection.value) _fiveMinutesHybridCheck.value else null,
                    tirePressureFrontRight = _tirePressureFrontRight.value.toDoubleOrNull(),
                    tirePressureFrontLeft = _tirePressureFrontLeft.value.toDoubleOrNull(),
                    tirePressureRearRight = _tirePressureRearRight.value.toDoubleOrNull(),
                    tirePressureRearLeft = _tirePressureRearLeft.value.toDoubleOrNull(),
                    userComments = _comments.value.takeIf { it.isNotBlank() }
                )

                val result = if (_existingPdi.value != null) {
                    // Update existing PDI
                    inspectionRepository.updateInspection(_existingPdi.value!!.pdiId, pdiRequest)
                } else {
                    // Create new PDI
                    inspectionRepository.createInspection(pdiRequest)
                }

                if (result.isSuccess) {
                    val pdi = result.getOrNull()
                    _success.value = "PDI saved successfully"
                    logTimber(tag, "PDI saved successfully with ID: ${pdi?.pdiId}")

                    // Upload images for the newly created PDI
                    val pdiId = pdi?.pdiId ?: return@launch
                    uploadImages(pdiId)

                } else {
                    logTimber(tag, "Failed to save PDI: ${result.exceptionOrNull()?.message}")
                    throw Exception("Failed to save PDI: ${result.exceptionOrNull()?.message}")
                }

            } catch (e: Exception) {
                _error.value = "Error saving PDI: ${e.message}"
                logTimber(tag, "Error saving PDI: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun uploadImages(pdiId: Int) {
        try {
            // Upload VIN images
            for (image in _vinImages.value) {
                uploadImage(pdiId, image, "vin")
            }

            // Upload SOC images
            for (image in _socImages.value) {
                uploadImage(pdiId, image, "soc")
            }

            // Upload Battery images
            for (image in _batteryImages.value) {
                uploadImage(pdiId, image, "battery")
            }

            // Upload Tire Pressure images
            for (image in _tireImages.value) {
                uploadImage(pdiId, image, "tire")
            }

            // Upload Extra images
            for (image in _extraImages.value) {
                uploadImage(pdiId, image, "extra")
            }

            logTimber(tag, "All images uploaded successfully")
        } catch (e: Exception) {
            logTimber(tag, "Error uploading images: ${e.message}")
            // Don't throw the exception as we still want the PDI to be saved
            // Just log the error
        }
    }

    private suspend fun uploadImage(pdiId: Int, image: ImageDTO, type: String) {
        // Implementation of image upload would go here
        // This would involve converting the image to a MultipartBody.Part
        // and calling the imageRoutes.uploadPdiImage() method

        // For now, we'll just log that we would upload the image
        logTimber(tag, "Would upload image of type $type to PDI $pdiId")

    }
}
