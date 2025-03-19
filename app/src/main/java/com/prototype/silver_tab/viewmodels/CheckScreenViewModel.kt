package com.prototype.silver_tab.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
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
import com.prototype.silver_tab.data.repository.ImageRepository
import com.prototype.silver_tab.data.repository.InspectionRepository
import com.prototype.silver_tab.session.AppSessionManager
import com.prototype.silver_tab.utils.getModelIdFromName
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError
import com.prototype.silver_tab.utils.validateNumericInput
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class CheckScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val appSessionManager: AppSessionManager,
    private val carRepository: CarRepository,
    private val imageRepository: ImageRepository,
    private val inspectionRepository: InspectionRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext private val appContext: Context
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

    private val _isInCorrectionMode = MutableStateFlow(false)
    val isInCorrectionMode: StateFlow<Boolean> = _isInCorrectionMode.asStateFlow()

    private val _isSuccessCorrection = MutableStateFlow(false)
    val isSuccessCorrection: StateFlow<Boolean> = _isSuccessCorrection.asStateFlow()

    private val _originalVin = MutableStateFlow<String?>(null)
    val originalVin: StateFlow<String?> = _originalVin.asStateFlow()

    init {
        // Get the route parameters
        val carChassi = savedStateHandle.get<String>("carChassi") ?: "new"
        val isNew = savedStateHandle.get<Boolean>("isNew") ?: false
        val isCorrection = savedStateHandle.get<Boolean>("isCorrection") ?: false

        logTimber(tag, "Initializing with carChassi: $carChassi, isNew: $isNew, isCorrection: $isCorrection")

        // If correction mode is set from the route, override the isInCorrectionMode value
        if (isCorrection) {
            _isInCorrectionMode.value = true
            logTimber(tag, "CORRECTION MODE ACTIVATED from route parameters")
        }

        _isNewCar.value = isNew

        // Load session data
        viewModelScope.launch {
            _isLoading.value = true  // Set loading state at the beginning

            try {
                // Retrieve sessionCar from the AppSessionManager
                val sessionCar = appSessionManager.selectedInspection.first()
                logTimber(tag, "Loaded session car: ${sessionCar?.name}, VIN: ${sessionCar?.vin}, PDI ID: ${sessionCar?.pdiId}, isCorrection: ${sessionCar?.isCorrection}")

                if (sessionCar == null) {
                    logTimberError(tag, "No car found in session data")
                    _isLoading.value = false
                    return@launch
                }

                // Update view model state with session data
                _selectedCar.value = sessionCar
                setupFeatureFlags(sessionCar.name)

                // Check if we're in correction mode from session data
                if (sessionCar.isCorrection || (sessionCar.pdiId != null && !isNew)) {
                    _isInCorrectionMode.value = true
                    _originalVin.value = sessionCar.vin
                    logTimber(tag, "CORRECTION MODE ACTIVATED for PDI ID: ${sessionCar.pdiId}")

                    // Pre-fill the form with existing values from the InspectionInfo
                    _vin.value = sessionCar.vin ?: ""
                    _socPercentage.value = sessionCar.soc?.toString() ?: ""
                    _battery12vVoltage.value = sessionCar.battery12v?.toString() ?: ""
                    _fiveMinutesHybridCheck.value = false // Default value
                    _tirePressureFrontRight.value = sessionCar.frontRightTire?.toString() ?: ""
                    _tirePressureFrontLeft.value = sessionCar.frontLeftTire?.toString() ?: ""
                    _tirePressureRearRight.value = sessionCar.rearRightTire?.toString() ?: ""
                    _tirePressureRearLeft.value = sessionCar.rearLeftTire?.toString() ?: ""
                    _comments.value = sessionCar.comments ?: ""

                    // Load existing images for the PDI being corrected
                    if (sessionCar.pdiId != null) {
                        logTimber(tag, "Loading existing images for PDI ID: ${sessionCar.pdiId}")
                        loadExistingImages(sessionCar.pdiId)
                    } else {
                        logTimberError(tag, "No PDI ID available to load images")
                        _isLoading.value = false
                    }
                } else if (isNew && carChassi != "new") {
                    // New PDI for an existing car with known VIN
                    _vin.value = carChassi
                    _isLoading.value = false
                } else {
                    // Completely new car/PDI or normal flow
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                logTimberError(tag, "Error during initialization: ${e.message}")
                e.printStackTrace() // Print full stack trace for debugging
                _error.value = "Error loading data: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun setupFeatureFlags(carName: String) {
        _needsBattery12vSection.value = carName.contains("DOLPHIN MINI", ignoreCase = true) ||
                carName.contains("YUAN PRO", ignoreCase = true)
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
            // Simulate loading PDI; implement as needed.
            _isLoading.value = false
        } catch (e: Exception) {
            _error.value = "Failed to load PDI: ${e.message}"
            _isLoading.value = false
        }
    }

    private suspend fun loadExistingImages(pdiId: Int) {
        _isLoading.value = true
        try {
            logTimber(tag, "FETCHING IMAGES from API for PDI ID: $pdiId")
            val images = imageRepository.getAllPdiImages(pdiId)
            logTimber(tag, "Successfully loaded ${images.size} images for PDI: $pdiId")

            // Clear existing images first
            _vinImages.value = emptyList()
            _socImages.value = emptyList()
            _batteryImages.value = emptyList()
            _tireImages.value = emptyList()
            _extraImages.value = emptyList()

            // Group images by type
            for (image in images) {
                logTimber(tag, "Processing image: ID=${image.imageId}, Type=${image.imageTypeName}, Path=${image.filePath}")

                when {
                    image.imageTypeName?.lowercase()?.contains("vin") == true ||
                            image.imageTypeName?.lowercase()?.contains("chassi") == true -> {
                        _vinImages.value = _vinImages.value + image
                        logTimber(tag, "Added to VIN images, count now: ${_vinImages.value.size}")
                    }
                    image.imageTypeName?.lowercase()?.contains("soc") == true -> {
                        _socImages.value = _socImages.value + image
                        logTimber(tag, "Added to SOC images, count now: ${_socImages.value.size}")
                    }
                    image.imageTypeName?.lowercase()?.contains("battery") == true -> {
                        _batteryImages.value = _batteryImages.value + image
                        logTimber(tag, "Added to Battery images, count now: ${_batteryImages.value.size}")
                    }
                    image.imageTypeName?.lowercase()?.contains("tire") == true -> {
                        _tireImages.value = _tireImages.value + image
                        logTimber(tag, "Added to Tire images, count now: ${_tireImages.value.size}")
                    }
                    else -> {
                        _extraImages.value = _extraImages.value + image
                        logTimber(tag, "Added to Extra images, count now: ${_extraImages.value.size}")
                    }
                }
            }

            // Add a small delay to ensure images are rendered properly
            delay(500)
            logTimber(tag, "Successfully processed existing PDI images")
        } catch (e: Exception) {
            logTimberError(tag, "Error loading existing images: ${e.message}")
            e.printStackTrace() // Print full stack trace for debugging
        } finally {
            _isLoading.value = false
        }
    }

    // Update functions for form fields
    fun updateVin(vin: String) {
        // Allow updating/validating VIN if we’re not in correction mode AND either the car is new or we're creating a new PDI.
        if (!_isInCorrectionMode.value && (_selectedCar.value?.carId == null || _isNewCar.value)) {
            _vin.value = vin
            validateVin(vin)
        }
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
        _vinError.value = if (vin.isBlank()) "VIN cannot be empty" else null
    }

    private fun validateSoc(soc: String) {
        if (soc.isBlank()) {
            _socError.value = null
            return
        }
        try {
            val socValue = soc.toDouble()
            _socError.value = if (socValue < 0 || socValue > 100) "SOC must be between 0 and 100%" else null
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
            _batteryError.value = if (voltageValue <= 0 || voltageValue > 15) "Battery voltage must be between 0 and 15V" else null
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
    fun processAndAddImage(section: String, uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val imageType = when (section) {
                    "vin" -> "CHASSI"
                    "soc" -> "SOC"
                    "battery" -> "BATTERY"
                    "tire" -> "TIRE"
                    "extra" -> "EXTRA"
                    else -> "EXTRA"
                }
                val pdiId = _existingPdi.value?.pdiId
                if (pdiId != null) {
                    // Upload image immediately
                    val result = imageRepository.uploadPdiImage(
                        pdiId = pdiId,
                        imageType = imageType,
                        imageUri = uri,
                        context = context
                    )
                    if (result.isSuccess) {
                        result.getOrNull()?.let { uploadedImage ->
                            addImage(section, uploadedImage)
                            _success.value = "Image uploaded successfully"
                        }
                    } else {
                        _error.value = "Failed to upload image: ${result.exceptionOrNull()?.message}"
                    }
                } else {
                    // If no PDI yet, add image locally
                    val tempImage = ImageDTO(
                        imageId = null,
                        pdiId = null,
                        imageTypeName = section,
                        fileName = uri.lastPathSegment,
                        filePath = uri.toString()
                    )
                    addImage(section, tempImage)
                }
            } catch (e: Exception) {
                _error.value = "Error processing image: ${e.message}"
                logTimberError(tag, "Error processing image: ${e.message}")
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
                val userId = authRepository.authState.value.userId ?: throw Exception("User not authenticated")
                val currentDate = SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault()).format(Date())

                // CORRECTION MODE LOGIC
                if (_isInCorrectionMode.value) {
                    // In correction mode, we MUST have a PDI ID to update
                    val pdiId = selectedCar.pdiId
                    if (pdiId == null) {
                        logTimberError(tag, "Cannot update in correction mode: No PDI ID found")
                        _isLoading.value = false
                        return@launch
                    }

                    val carId = selectedCar.carId
                    if (carId == null) {
                        logTimberError(tag, "Cannot update in correction mode: No car ID found")
                        _isLoading.value = false
                        return@launch
                    }

                    logTimber(tag, "Updating existing PDI #$pdiId for car #$carId in correction mode")

                    // Create the PDI update request
                    val pdiRequest = PdiRequest(
                        pdiId = pdiId, // IMPORTANT: Include the PDI ID for update operations
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

                    // Call the update method directly
                    val result = inspectionRepository.updateInspection(pdiId, pdiRequest)

                    if (result.isSuccess) {
                        val pdi = result.getOrNull()
                        logTimber(tag, "PDI updated successfully with ID: ${pdi?.pdiId}")

                        // Set up for success dialog
                        _isSuccessCorrection.value = true

                        // Upload images - here we pass the SAME pdiId as the
                        // one we're updating, since we're not creating a new record
                        uploadImagesAndShowSuccess(pdiId, null)
                    } else {
                        logTimberError(tag, "Failed to update PDI: ${result.exceptionOrNull()?.message}")
                        _isLoading.value = false
                    }

                    return@launch
                }

                // NON-CORRECTION MODE (NEW PDI) LOGIC BELOW
                val dealerCode = appSessionManager.selectedDealer.value?.dealerCode
                    ?: throw Exception("No dealer found")

                // For new car, first check if VIN already exists
                if (_isNewCar.value && vin.value.isNotBlank()) {
                    logTimber(tag, "Checking if VIN ${vin.value} already exists")
                    val existingCar = carRepository.getCarByVin(vin.value)

                    if (existingCar != null) {
                        // VIN already exists in system
                        if (existingCar.dealerCode == dealerCode) {
                            // VIN exists for this dealer - use existing car
                            logTimber(tag, "VIN ${vin.value} already exists for this dealer. Using existing car.")
                            _error.value = "VIN ${vin.value} already exists for this dealer. Using existing car."
                            delay(2000) // Brief delay to show message

                            // Create PDI with existing car ID
                            createPdiForCar(existingCar.carId, userId, currentDate)
                        } else {
                            // VIN exists but for a different dealer
                            logTimberError(tag, "VIN ${vin.value} is already registered with another dealer")
                            _error.value = "This VIN is already registered with another dealer"
                            _isLoading.value = false
                            return@launch
                        }
                    } else {
                        // VIN doesn't exist, create new car
                        logTimber(tag, "VIN ${vin.value} doesn't exist. Creating new car.")
                        createNewCarAndPdi(selectedCar, userId, currentDate, dealerCode)
                    }
                } else if (selectedCar.carId != null) {
                    // Create new PDI for existing car
                    createPdiForCar(selectedCar.carId, userId, currentDate)
                } else {
                    throw Exception("Cannot determine car ID for PDI creation")
                }
            } catch (e: Exception) {
                _error.value = "Error ${if (_isInCorrectionMode.value) "updating" else "saving"} PDI: ${e.message}"
                logTimberError(tag, "Error ${if (_isInCorrectionMode.value) "updating" else "saving"} PDI: ${e.message}")
                _isLoading.value = false
            }
        }
    }

    // Helper method to create a new car and then create a PDI for it
    private suspend fun createNewCarAndPdi(
        selectedCar: InspectionInfo,
        userId: Int,
        currentDate: String,
        dealerCode: String
    ) {
        val carModelId = getModelIdFromName(selectedCar.name)
            ?: throw Exception("Invalid car model")

        val carRequest = CarResponse(
            carId = null,
            createdAt = currentDate,
            carModelId = carModelId,
            carModelName = selectedCar.name,
            dealerCode = dealerCode,
            vin = _vin.value,
            isSold = false,
            updatedAt = null
        )

        val carResult = carRepository.createCar(carRequest)
        if (!carResult.isSuccess) {
            throw Exception("Failed to create car: ${carResult.exceptionOrNull()?.message}")
        }

        // Get the new car ID and create PDI
        val newCarId = carResult.getOrNull()?.carId
            ?: throw Exception("Car created but no ID returned")

        logTimber(tag, "New car created with ID: $newCarId")
        createPdiForCar(newCarId, userId, currentDate)
    }

    // Helper method to create a PDI for an existing car
    private suspend fun createPdiForCar(
        carId: Int,
        userId: Int,
        currentDate: String
    ) {
        // Create the PDI request
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

        val result = inspectionRepository.createInspection(pdiRequest)

        if (result.isSuccess) {
            val pdi = result.getOrNull()
            logTimber(tag, "PDI created successfully with ID: ${pdi?.pdiId}")

            // Set success flag
            _isSuccessCorrection.value = false

            // Upload images for the newly created PDI
            pdi?.pdiId?.let { newPdiId ->
                uploadImagesAndShowSuccess(newPdiId, null)
            }
        } else {
            logTimberError(tag, "Failed to create PDI: ${result.exceptionOrNull()?.message}")
            throw Exception("Failed to create PDI: ${result.exceptionOrNull()?.message}")
        }
    }

    // Helper method to update an existing PDI
    private suspend fun updateExistingPdi(
        pdiId: Int,
        carId: Int,
        userId: Int,
        currentDate: String
    ) {
        // Create the PDI request
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

        val result = inspectionRepository.updateInspection(pdiId, pdiRequest)
        handlePdiResult(result)
    }

    // Helper method to handle the PDI creation/update result
    private suspend fun handlePdiResult(result: Result<PDI>) {
        if (result.isSuccess) {
            val pdi = result.getOrNull()
            logTimber(tag, "PDI ${if (_isInCorrectionMode.value) "updated" else "saved"} successfully with ID: ${pdi?.pdiId}")
            val pdiId = pdi?.pdiId ?: return

            // Upload images and show success dialog when complete
            uploadImagesAndShowSuccess(pdiId, _originalVin.value?.let { pdi.pdiId })
        } else {
            val errorMessage = "Failed to ${if (_isInCorrectionMode.value) "update" else "save"} PDI: ${result.exceptionOrNull()?.message}"
            logTimberError(tag, errorMessage)

            // In correction mode, don't throw an exception, just log it and set isLoading to false
            if (_isInCorrectionMode.value) {
                _isLoading.value = false
            } else {
                throw Exception(errorMessage)
            }
        }
    }

    private suspend fun uploadImagesAndShowSuccess(pdiId: Int, originalPdiId: Int? = null) {
        try {
            var totalImages = _vinImages.value.size +
                    _socImages.value.size +
                    _batteryImages.value.size +
                    _tireImages.value.size +
                    _extraImages.value.size

            var uploadedImages = 0
            var failedUploads = 0

            // In correction mode, originalPdiId is provided but it's the same as pdiId
            val isCorrection = _isInCorrectionMode.value
            logTimber(tag, "Starting image processing for PDI ID: $pdiId, correction mode: $isCorrection")

            // First, handle existing images that need to be deleted (if in correction mode)
            if (isCorrection) {
                try {
                    // In correction mode, we're updating the SAME PDI record
                    logTimber(tag, "CORRECTION MODE - Handling existing images for PDI ID: $pdiId")

                    // Debug: Forcefully fetch ALL images for this PDI to confirm they exist in the database
                    val existingImages = imageRepository.getAllPdiImages(pdiId)
                    logTimber(tag, "FOUND ${existingImages.size} EXISTING IMAGES IN DATABASE for PDI: $pdiId")

                    // Log all images in detail
                    existingImages.forEach { image ->
                        logTimber(tag, "Database image: ID=${image.imageId}, Type=${image.imageTypeName}, Path=${image.filePath}")
                    }

                    // Create maps for UI state image IDs for easier lookup
                    val currentImageIds = mutableMapOf<String, Set<Int>>()
                    currentImageIds["vin"] = _vinImages.value.mapNotNull { it.imageId }.toSet()
                    currentImageIds["soc"] = _socImages.value.mapNotNull { it.imageId }.toSet()
                    currentImageIds["battery12v"] = _batteryImages.value.mapNotNull { it.imageId }.toSet()
                    currentImageIds["tire"] = _tireImages.value.mapNotNull { it.imageId }.toSet()
                    currentImageIds["extra"] = _extraImages.value.mapNotNull { it.imageId }.toSet()

                    // Log current image IDs for debugging
                    currentImageIds.forEach { (type, ids) ->
                        logTimber(tag, "UI current $type image IDs: $ids")
                    }

                    // Directly find images to delete by comparing database vs UI
                    val imagesToDelete = mutableListOf<Int>()

                    for (image in existingImages) {
                        val imageId = image.imageId
                        if (imageId == null) {
                            logTimber(tag, "Skipping image with null ID")
                            continue
                        }

                        val imageType = image.imageTypeName?.lowercase() ?: ""
                        val typeForLookup = when {
                            imageType.contains("vin") || imageType.contains("chassi") -> "vin"
                            imageType.contains("soc") -> "soc"
                            imageType.contains("battery") -> "battery12v"
                            imageType.contains("tire") -> "tire"
                            else -> "extra"
                        }

                        val isInCurrentUI = currentImageIds[typeForLookup]?.contains(imageId) ?: false

                        if (!isInCurrentUI) {
                            imagesToDelete.add(imageId)
                            logTimber(tag, "MARKING IMAGE ${imageId} (type: ${image.imageTypeName}) FOR DELETION")
                        } else {
                            logTimber(tag, "Keeping image ${imageId} (type: ${image.imageTypeName})")
                        }
                    }

                    logTimber(tag, "FOUND ${imagesToDelete.size} IMAGES TO DELETE")

                    // Delete the images one by one
                    for (imageId in imagesToDelete) {
                        try {
                            logTimber(tag, "CALLING DELETE API for image: $imageId")
                            val result = imageRepository.deletePdiImage(imageId)

                            if (result.isSuccess) {
                                logTimber(tag, "Successfully deleted image: $imageId")
                            } else {
                                logTimberError(tag, "Failed to delete image $imageId: ${result.exceptionOrNull()?.message}")
                            }
                        } catch (e: Exception) {
                            logTimberError(tag, "Exception deleting image $imageId: ${e.message}")
                            e.printStackTrace() // Print full stack trace for debugging
                        }
                    }
                } catch (e: Exception) {
                    logTimberError(tag, "Error handling existing images: ${e.message}")
                    e.printStackTrace() // Print full stack trace for debugging
                }
            } else {
                logTimber(tag, "Skipping image deletion - not in correction mode")
            }

            // Now upload new images (those without imageId)
            logTimber(tag, "Uploading new images to PDI ID: $pdiId")

            // Count how many images need to be uploaded
            val newImagesToUpload = _vinImages.value.count { it.imageId == null } +
                    _socImages.value.count { it.imageId == null } +
                    _batteryImages.value.count { it.imageId == null } +
                    _tireImages.value.count { it.imageId == null } +
                    _extraImages.value.count { it.imageId == null }

            logTimber(tag, "Found $newImagesToUpload new images to upload")

            // VIN images
            for (image in _vinImages.value.filter { it.imageId == null }) {
                logTimber(tag, "Uploading new VIN image")
                val result = uploadImage(pdiId, image, "vin", appContext)
                if (result.isSuccess) uploadedImages++ else failedUploads++
            }

            // SOC images
            for (image in _socImages.value.filter { it.imageId == null }) {
                logTimber(tag, "Uploading new SOC image")
                val result = uploadImage(pdiId, image, "soc", appContext)
                if (result.isSuccess) uploadedImages++ else failedUploads++
            }

            // Battery images
            for (image in _batteryImages.value.filter { it.imageId == null }) {
                logTimber(tag, "Uploading new Battery image")
                val result = uploadImage(pdiId, image, "battery12V", appContext)
                if (result.isSuccess) uploadedImages++ else failedUploads++
            }

            // Tire images
            for (image in _tireImages.value.filter { it.imageId == null }) {
                logTimber(tag, "Uploading new Tire image")
                val result = uploadImage(pdiId, image, "tire", appContext)
                if (result.isSuccess) uploadedImages++ else failedUploads++
            }

            // Extra images
            for (image in _extraImages.value.filter { it.imageId == null }) {
                logTimber(tag, "Uploading new Extra image")
                val result = uploadImage(pdiId, image, "extraImages", appContext)
                if (result.isSuccess) uploadedImages++ else failedUploads++
            }

            logTimber(tag, "Image processing complete. Uploaded: $uploadedImages, Failed: $failedUploads, Total images: $totalImages")

            // Set the correction flag for success dialog based on the current mode
            _isSuccessCorrection.value = isCorrection

            // Now show the success message with different text for correction mode
            if (failedUploads > 0) {
                _success.value = if (isCorrection)
                    "PDI updated but $failedUploads out of $totalImages images failed to upload"
                else
                    "PDI saved but $failedUploads out of $totalImages images failed to upload"
            } else {
                _success.value = if (isCorrection)
                    "PDI and all images updated successfully"
                else
                    "PDI and all images saved successfully"
            }
        } catch (e: Exception) {
            logTimberError(tag, "Error during image processing: ${e.message}")
            e.printStackTrace() // Print full stack trace for debugging
            _success.value = if (_isInCorrectionMode.value)
                "PDI updated but image processing failed: ${e.message}"
            else
                "PDI saved but image processing failed: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun uploadImages(pdiId: Int) {
        try {
            for (image in _vinImages.value) {
                uploadImage(pdiId, image, "vin", appContext)
            }
            for (image in _socImages.value) {
                uploadImage(pdiId, image, "soc", appContext)
            }
            for (image in _batteryImages.value) {
                uploadImage(pdiId, image, "battery12V", appContext)
            }
            for (image in _tireImages.value) {
                uploadImage(pdiId, image, "tire", appContext)
            }
            for (image in _extraImages.value) {
                uploadImage(pdiId, image, "extraImages", appContext)
            }
            logTimber(tag, "All images uploaded successfully")
        } catch (e: Exception) {
            logTimber(tag, "Error uploading images: ${e.message}")
        }
    }

    private suspend fun uploadImage(pdiId: Int, image: ImageDTO, type: String, context: Context): Result<ImageDTO> {
        return try {
            // Check if this is an existing image that already has an ID
            if (image.imageId != null) {
                // Already exists in database, no need to upload again
                logTimber(tag, "Image already exists in DB with ID: ${image.imageId}, skipping upload")
                return Result.success(image)
            }

            // Handle new images being uploaded
            if (image.filePath == null) {
                logTimberError(tag, "Image file path is null for $type image")
                return Result.failure(Exception("Image file path is null"))
            }

            val imageUri = Uri.parse(image.filePath)

            // Validate that the URI is accessible before attempting to upload
            if (!isUriReadable(imageUri, context)) {
                logTimberError(tag, "Cannot read image file at URI: $imageUri")
                return Result.failure(Exception("Failed to read image file"))
            }

            val result = imageRepository.uploadPdiImage(pdiId, type, imageUri, context)
            if (result.isSuccess) {
                logTimber(tag, "Successfully uploaded image of type $type with ID: ${result.getOrNull()?.imageId}")
            } else {
                logTimberError(tag, "Failed to upload image of type $type: ${result.exceptionOrNull()?.message}")
            }
            result
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logTimberError(tag, "Exception during image upload: ${e.message}")
            Result.failure(e)
        }
    }

    // Helper method to check if a URI is readable
    private fun isUriReadable(uri: Uri, context: Context): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use {
                // Just checking if we can open the stream
                true
            } ?: false
        } catch (e: Exception) {
            logTimberError(tag, "Error checking URI readability: ${e.message}")
            false
        }
    }
}
