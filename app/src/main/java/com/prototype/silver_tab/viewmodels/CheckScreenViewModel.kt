package com.prototype.silver_tab.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.repository.CheckScreenRepository
import com.prototype.silver_tab.data.repository.ImageRepository
import com.prototype.silver_tab.data.repository.SharedCarRepository
import com.prototype.silver_tab.ui.components.checkscreen.ImageType
import com.prototype.silver_tab.utils.validation.CheckScreenValidator
import com.prototype.silver_tab.utils.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * State class for the Check Screen
 */
data class CheckScreenState(
    // Form data
    val pdiId: Int? = null, // Will be set when correcting an existing PDI
    val chassisNumber: String = "",
    val batteryVoltage: String = "",
    val socPercentage: String = "",
    val isCarStarted: Boolean = false,
    val additionalInfo: String = "",

    // Tire pressure values
    val frontLeftPressure: String = "",
    val frontRightPressure: String = "",
    val rearLeftPressure: String = "",
    val rearRightPressure: String = "",

    // Image URIs - Lists
    val chassisImageUris: List<Uri> = emptyList(),
    val socImageUris: List<Uri> = emptyList(),
    val battery12VImageUris: List<Uri> = emptyList(),
    val tirePressureImageUris: List<Uri> = emptyList(),
    val extraImageUris: List<Uri> = emptyList(),

    // Dialog states
    val showCancelDialog: Boolean = false,
    val showFinishDialog: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val showDuplicateVinDialog: Boolean = false,

    // Loading state
    val isLoadingImages: Boolean = false,

    val isCheckingVinExists: Boolean = false,
    val vinAlreadyExists: Boolean = false,
    val vinExistsError: String? = null,

    // This map is kept for backward compatibility
    val imageIdMap: MutableMap<Uri, Int> = mutableMapOf(),
    val deletedImageIds: MutableSet<Int> = mutableSetOf()
)

/**
 * Events that can be triggered from the UI
 */
sealed class CheckScreenEvent {
    // Text field updates
    data class UpdateChassisNumber(val value: String) : CheckScreenEvent()
    data class UpdateSocPercentage(val value: String) : CheckScreenEvent()
    data class UpdateBatteryVoltage(val value: String) : CheckScreenEvent()
    data class UpdateFrontLeftPressure(val value: String) : CheckScreenEvent()
    data class UpdateFrontRightPressure(val value: String) : CheckScreenEvent()
    data class UpdateRearLeftPressure(val value: String) : CheckScreenEvent()
    data class UpdateRearRightPressure(val value: String) : CheckScreenEvent()
    data class UpdateAdditionalInfo(val value: String) : CheckScreenEvent()
    data class UpdateCarStarted(val value: Boolean) : CheckScreenEvent()

    // Image operations
    data class AddImage(val type: ImageType, val uri: Uri) : CheckScreenEvent()
    data class RemoveImage(val type: ImageType, val index: Int) : CheckScreenEvent()
    data class SetLoadingImages(val isLoading: Boolean) : CheckScreenEvent()

    // Dialog controls
    object ShowCancelDialog : CheckScreenEvent()
    object HideCancelDialog : CheckScreenEvent()
    object ShowFinishDialog : CheckScreenEvent()
    object HideFinishDialog : CheckScreenEvent()
    object ShowSuccessDialog : CheckScreenEvent()
    object HideSuccessDialog : CheckScreenEvent()
    object ShowDuplicateVinDialog : CheckScreenEvent()
    object HideDuplicateVinDialog : CheckScreenEvent()

    // PDI submission
    data class SubmitPdi(
        val context: Context,
        val userId: Long,
        val dealerCode: String,
        val modelId: Int? = null,
        val isCorrection: Boolean = false
    ) : CheckScreenEvent()

    // Navigation
    object NavigateBack : CheckScreenEvent()
    object NavigateToHome : CheckScreenEvent()

    // Initialization
    data class InitializeWithCar(val car: InspectionInfo) : CheckScreenEvent()


    // Image loading
    data class LoadExistingImages(val pdiId: Int, val context: Context) : CheckScreenEvent()
}

class ImageChangeTracker {
    private val newImages = mutableMapOf<String, MutableList<Uri>>()
    private val deletedImageIds = mutableSetOf<Int>()
    private val imageIdMap = mutableMapOf<Uri, Int>()
    private val imageTypeMap = mutableMapOf<Uri, String>()

    fun addNewImage(type: String, uri: Uri) {
        val imagesOfType = newImages.getOrPut(type) { mutableListOf() }
        imagesOfType.add(uri)
    }

    fun markImageDeleted(imageId: Int) {
        deletedImageIds.add(imageId)
        // Also remove from imageIdMap to keep things consistent
        imageIdMap.entries.find { it.value == imageId }?.let {
            imageIdMap.remove(it.key)
        }
    }

    fun trackImageId(uri: Uri, imageId: Int, imageType: String) {
        imageIdMap[uri] = imageId
        imageTypeMap[uri] = imageType
    }

    fun getImageId(uri: Uri): Int? = imageIdMap[uri]
    fun getImageType(uri: Uri): String? = imageTypeMap[uri]
    fun getNewImagesOfType(type: String): List<Uri> = newImages[type] ?: emptyList()
    fun getDeletedImageIds(): Set<Int> = deletedImageIds
    fun hasExistingImage(uri: Uri): Boolean = imageIdMap.containsKey(uri)
}

/**
 * ViewModel for the Check Screen
 * Uses the repository pattern and clean architecture principles
 * Updated to use Hilt for DI and SharedCarRepository
 */
@HiltViewModel
class CheckScreenViewModel @Inject constructor(
    private val repository: CheckScreenRepository,
    private val sharedCarRepository: SharedCarRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    // State management
    private val _state = MutableStateFlow(CheckScreenState())
    val state: StateFlow<CheckScreenState> = _state.asStateFlow()

    // Track image changes
    private val imageTracker = ImageChangeTracker()

    // Validation state
    private val _validationState = MutableStateFlow<Map<String, ValidationResult>>(emptyMap())
    val validationState: StateFlow<Map<String, ValidationResult>> = _validationState.asStateFlow()

    // Submission status
    private val _submissionState = MutableStateFlow<SubmissionState>(SubmissionState.Idle)
    val submissionState: StateFlow<SubmissionState> = _submissionState.asStateFlow()

    /**
     * Handle events from the UI
     */
    fun handleEvent(event: CheckScreenEvent) {
        when (event) {
            // Text field updates
            is CheckScreenEvent.UpdateChassisNumber -> updateChassisNumber(event.value)
            is CheckScreenEvent.UpdateSocPercentage -> updateSocPercentage(event.value)
            is CheckScreenEvent.UpdateBatteryVoltage -> updateBatteryVoltage(event.value)
            is CheckScreenEvent.UpdateFrontLeftPressure -> updateFrontLeftPressure(event.value)
            is CheckScreenEvent.UpdateFrontRightPressure -> updateFrontRightPressure(event.value)
            is CheckScreenEvent.UpdateRearLeftPressure -> updateRearLeftPressure(event.value)
            is CheckScreenEvent.UpdateRearRightPressure -> updateRearRightPressure(event.value)
            is CheckScreenEvent.UpdateAdditionalInfo -> updateAdditionalInfo(event.value)
            is CheckScreenEvent.UpdateCarStarted -> updateCarStarted(event.value)

            // Image operations
            is CheckScreenEvent.AddImage -> addImage(event.type, event.uri)
            is CheckScreenEvent.RemoveImage -> removeImage(event.type, event.index)
            is CheckScreenEvent.SetLoadingImages -> setLoadingImages(event.isLoading)

            // Dialog controls
            is CheckScreenEvent.ShowCancelDialog -> showCancelDialog()
            is CheckScreenEvent.HideCancelDialog -> hideCancelDialog()
            is CheckScreenEvent.ShowFinishDialog -> showFinishDialog()
            is CheckScreenEvent.HideFinishDialog -> hideFinishDialog()
            is CheckScreenEvent.ShowSuccessDialog -> showSuccessDialog()
            is CheckScreenEvent.HideSuccessDialog -> hideSuccessDialog()
            is CheckScreenEvent.ShowDuplicateVinDialog -> showDuplicateVinDialog()
            is CheckScreenEvent.HideDuplicateVinDialog -> hideDuplicateVinDialog()

            // PDI submission
            is CheckScreenEvent.SubmitPdi -> submitPdi(
                event.context,
                event.userId,
                event.dealerCode,
                event.modelId,
                event.isCorrection
            )

            // Initialization
            is CheckScreenEvent.InitializeWithCar -> initializeWithCar(event.car)

            // Image loading
            is CheckScreenEvent.LoadExistingImages -> loadExistingPdiImages(event.pdiId, event.context)

            // Navigation
            is CheckScreenEvent.NavigateBack -> {
                // Navigation is handled in the composable
            }
            is CheckScreenEvent.NavigateToHome -> {
                // Navigation is handled in the composable
            }
        }
    }

    // All other methods remain the same

    /**
     * Show duplicate VIN dialog
     */
    private fun showDuplicateVinDialog() {
        _state.update { it.copy(showDuplicateVinDialog = true) }
    }

    /**
     * Hide duplicate VIN dialog
     */
    private fun hideDuplicateVinDialog() {
        _state.update { it.copy(showDuplicateVinDialog = false) }
    }

    /**
     * Check if VIN already exists in the database
     * @return True if VIN exists, false otherwise
     */
    suspend fun checkVinExists(vin: String): Boolean {
        return try {
            val result = repository.vinExists(vin)
            result.getOrNull() ?: false
        } catch (e: Exception) {
            Timber.e(e, "Error checking VIN existence")
            false
        }
    }

    /**
     * Validate VIN before submission
     * @return True if validation passes, false otherwise
     */
    suspend fun validateVinBeforeSubmission(): Boolean {
        val vin = _state.value.chassisNumber

        // First apply basic validation
        val validationResult = CheckScreenValidator.validateChassisNumber(vin)
        if (validationResult is ValidationResult.Error) {
            return false
        }

        // Then check if VIN already exists
        val vinExists = checkVinExists(vin)
        if (vinExists) {
            showDuplicateVinDialog()
            return false
        }

        return true
    }

    /**
     * Validate the form
     * @param requireBattery12V Whether 12V battery voltage is required
     * @param skipChassisValidation Whether to skip chassis validation (for existing cars)
     * @return True if form is valid, false otherwise
     */
    fun validateForm(requireBattery12V: Boolean, skipChassisValidation: Boolean = false): Boolean {
        val validationResults = mutableMapOf<String, ValidationResult>()

        // Only validate chassis if not skipping
        if (!skipChassisValidation) {
            validationResults["chassisNumber"] = CheckScreenValidator.validateChassisNumber(_state.value.chassisNumber)
        } else {
            // Automatically consider chassis valid if skipping validation
            validationResults["chassisNumber"] = ValidationResult.Success
        }

        // Always validate these fields
        validationResults["socPercentage"] = CheckScreenValidator.validateSocPercentage(_state.value.socPercentage)
        validationResults["frontLeftPressure"] = CheckScreenValidator.validateTirePressure(_state.value.frontLeftPressure)
        validationResults["frontRightPressure"] = CheckScreenValidator.validateTirePressure(_state.value.frontRightPressure)
        validationResults["rearLeftPressure"] = CheckScreenValidator.validateTirePressure(_state.value.rearLeftPressure)
        validationResults["rearRightPressure"] = CheckScreenValidator.validateTirePressure(_state.value.rearRightPressure)

        if (requireBattery12V) {
            validationResults["batteryVoltage"] = CheckScreenValidator.validateBatteryVoltage(_state.value.batteryVoltage)
        }

        _validationState.value = validationResults
        return CheckScreenValidator.isFormValid(validationResults)
    }

    /**
     * Update chassis number with validation
     */
    private fun updateChassisNumber(value: String) {
        _state.update { it.copy(chassisNumber = value) }
        val validationResult = CheckScreenValidator.validateChassisNumber(value)
        updateValidationState("chassisNumber", validationResult)
    }

    /**
     * Update SOC percentage with validation and formatting
     */
    private fun updateSocPercentage(value: String) {
        val formattedValue = CheckScreenValidator.formatNumericInput(value)
        _state.update { it.copy(socPercentage = formattedValue) }
        val validationResult = CheckScreenValidator.validateSocPercentage(formattedValue)
        updateValidationState("socPercentage", validationResult)
    }

    /**
     * Update battery voltage with validation and formatting
     */
    private fun updateBatteryVoltage(value: String) {
        val formattedValue = CheckScreenValidator.formatNumericInput(value)
        _state.update { it.copy(batteryVoltage = formattedValue) }
        val validationResult = CheckScreenValidator.validateBatteryVoltage(formattedValue)
        updateValidationState("batteryVoltage", validationResult)
    }

    /**
     * Update tire pressure values with validation and formatting
     */
    private fun updateFrontLeftPressure(value: String) {
        val formattedValue = CheckScreenValidator.formatNumericInput(value)
        _state.update { it.copy(frontLeftPressure = formattedValue) }
        val validationResult = CheckScreenValidator.validateTirePressure(formattedValue)
        updateValidationState("frontLeftPressure", validationResult)
    }

    private fun updateFrontRightPressure(value: String) {
        val formattedValue = CheckScreenValidator.formatNumericInput(value)
        _state.update { it.copy(frontRightPressure = formattedValue) }
        val validationResult = CheckScreenValidator.validateTirePressure(formattedValue)
        updateValidationState("frontRightPressure", validationResult)
    }

    private fun updateRearLeftPressure(value: String) {
        val formattedValue = CheckScreenValidator.formatNumericInput(value)
        _state.update { it.copy(rearLeftPressure = formattedValue) }
        val validationResult = CheckScreenValidator.validateTirePressure(formattedValue)
        updateValidationState("rearLeftPressure", validationResult)
    }

    private fun updateRearRightPressure(value: String) {
        val formattedValue = CheckScreenValidator.formatNumericInput(value)
        _state.update { it.copy(rearRightPressure = formattedValue) }
        val validationResult = CheckScreenValidator.validateTirePressure(formattedValue)
        updateValidationState("rearRightPressure", validationResult)
    }

    /**
     * Update additional info field
     */
    private fun updateAdditionalInfo(value: String) {
        _state.update { it.copy(additionalInfo = value) }
    }

    /**
     * Update car started status
     */
    private fun updateCarStarted(value: Boolean) {
        _state.update { it.copy(isCarStarted = value) }
    }

    /**
     * Update loading images state
     */
    private fun setLoadingImages(isLoading: Boolean) {
        _state.update { it.copy(isLoadingImages = isLoading) }
    }

    /**
     * Update a validation state for a specific field
     */
    private fun updateValidationState(field: String, result: ValidationResult) {
        _validationState.update { currentMap ->
            val mutableMap = currentMap.toMutableMap()
            mutableMap[field] = result
            mutableMap
        }
    }

    /**
     * Add an image to the appropriate list based on type
     */
    private fun addImage(type: ImageType, uri: Uri) {
        _state.update { currentState ->
            when (type) {
                ImageType.CHASSIS -> currentState.copy(
                    chassisImageUris = currentState.chassisImageUris + uri
                )
                ImageType.SOC -> currentState.copy(
                    socImageUris = currentState.socImageUris + uri
                )
                ImageType.BATTERY_12VOLTAGE -> currentState.copy(
                    battery12VImageUris = currentState.battery12VImageUris + uri
                )
                ImageType.TIRE_PRESSURE -> currentState.copy(
                    tirePressureImageUris = currentState.tirePressureImageUris + uri
                )
                ImageType.EXTRA_IMAGE -> currentState.copy(
                    extraImageUris = currentState.extraImageUris + uri
                )
            }
        }

        // Add to tracking for new images
        val typeString = when (type) {
            ImageType.CHASSIS -> "vin"
            ImageType.SOC -> "soc"
            ImageType.BATTERY_12VOLTAGE -> "battery12V"
            ImageType.TIRE_PRESSURE -> "tire"
            ImageType.EXTRA_IMAGE -> "extraImages"
        }

        // Only track if it's a new image (not one loaded from the server)
        if (!imageTracker.hasExistingImage(uri)) {
            imageTracker.addNewImage(typeString, uri)
        }
    }

    /**
     * Remove an image from the appropriate list based on type
     */
    private fun removeImage(type: ImageType, index: Int) {
        _state.update { currentState ->
            val imageUris = when (type) {
                ImageType.CHASSIS -> currentState.chassisImageUris
                ImageType.SOC -> currentState.socImageUris
                ImageType.BATTERY_12VOLTAGE -> currentState.battery12VImageUris
                ImageType.TIRE_PRESSURE -> currentState.tirePressureImageUris
                ImageType.EXTRA_IMAGE -> currentState.extraImageUris
            }

            if (index < imageUris.size) {
                val uri = imageUris[index]

                // Check if the image has an ID (was loaded from server)
                val imageId = imageTracker.getImageId(uri)
                if (imageId != null) {
                    // If it has an ID, mark it for deletion
                    imageTracker.markImageDeleted(imageId)
                    Timber.d("Marking image for deletion: ID $imageId")
                }

                // Create the new state with the image removed
                when (type) {
                    ImageType.CHASSIS -> currentState.copy(
                        chassisImageUris = imageUris.filterIndexed { i, _ -> i != index }
                    )
                    ImageType.SOC -> currentState.copy(
                        socImageUris = imageUris.filterIndexed { i, _ -> i != index }
                    )
                    ImageType.BATTERY_12VOLTAGE -> currentState.copy(
                        battery12VImageUris = imageUris.filterIndexed { i, _ -> i != index }
                    )
                    ImageType.TIRE_PRESSURE -> currentState.copy(
                        tirePressureImageUris = imageUris.filterIndexed { i, _ -> i != index }
                    )
                    ImageType.EXTRA_IMAGE -> currentState.copy(
                        extraImageUris = imageUris.filterIndexed { i, _ -> i != index }
                    )
                }
            } else {
                currentState
            }
        }
    }

    /**
     * Track an image ID for an image loaded from the server
     */
    fun trackImageId(uri: Uri, imageId: Int, imageType: String = "") {
        imageTracker.trackImageId(uri, imageId, imageType)

        // Also update the state's imageIdMap for backward compatibility
        _state.update { currentState ->
            val updatedMap = currentState.imageIdMap.toMutableMap()
            updatedMap[uri] = imageId
            currentState.copy(imageIdMap = updatedMap)
        }
    }

    /**
     * Get all image IDs that were marked for deletion
     */
    fun getDeletedImageIds(): Set<Int> {
        return imageTracker.getDeletedImageIds()
    }

    /**
     * Load existing PDI images for a correction
     */
    private fun loadExistingPdiImages(pdiId: Int, context: Context) {
        setLoadingImages(true)

        viewModelScope.launch {
            try {
                // Store the PDI ID in state
                _state.update { it.copy(pdiId = pdiId) }

                Timber.d("Loading existing images for PDI ID: $pdiId")
                val pdiImages = imageRepository.getAllPdiImages(pdiId) ?: emptyList()
                Timber.d("Loaded ${pdiImages.size} images from server")

                // Process images in batches to avoid memory issues
                val batchSize = 2
                pdiImages.chunked(batchSize).forEach { batch ->
                    batch.forEach { imageDTO ->
                        try {
                            // Skip images with no ID
                            val imageId = imageDTO.imageId ?: return@forEach
                            val imageTypeName = imageDTO.imageTypeName ?: return@forEach

                            // Map API image type to our ImageType enum
                            val imageType = when {
                                imageTypeName.contains("vin", ignoreCase = true) ->
                                    ImageType.CHASSIS
                                imageTypeName.contains("soc", ignoreCase = true) ->
                                    ImageType.SOC
                                imageTypeName.contains("tire", ignoreCase = true) ->
                                    ImageType.TIRE_PRESSURE
                                imageTypeName.contains("battery", ignoreCase = true) ->
                                    ImageType.BATTERY_12VOLTAGE
                                imageTypeName.contains("extraImages", ignoreCase = true) ->
                                    ImageType.EXTRA_IMAGE
                                else -> null
                            }

                            if (imageType != null) {
                                // Convert to URI
                                val uri = imageRepository.convertImageDtoToUri(imageDTO)

                                uri?.let {
                                    // Track the image ID
                                    trackImageId(uri, imageId, imageTypeName)

                                    // Add to the appropriate image list
                                    addImage(imageType, uri)

                                    Timber.d("Loaded image: ID $imageId, Type $imageTypeName")
                                }
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error processing image: ${e.message}")
                        }
                    }
                }

            } catch (e: Exception) {
                Timber.e(e, "Error loading PDI images: ${e.message}")
            } finally {
                setLoadingImages(false)
            }
        }
    }

    /* Dialog Management Methods */

    private fun showCancelDialog() {
        _state.update { it.copy(showCancelDialog = true) }
    }

    private fun hideCancelDialog() {
        _state.update { it.copy(showCancelDialog = false) }
    }

    private fun showFinishDialog() {
        _state.update { it.copy(showFinishDialog = true) }
    }

    private fun hideFinishDialog() {
        _state.update { it.copy(showFinishDialog = false) }
    }

    private fun showSuccessDialog() {
        _state.update { it.copy(showSuccessDialog = true) }
    }

    private fun hideSuccessDialog() {
        _state.update { it.copy(showSuccessDialog = false) }
    }

    /**
     * Initialize the form with data from an existing car
     */
    private fun initializeWithCar(car: InspectionInfo) {
        _state.update { currentState ->
            currentState.copy(
                chassisNumber = car.chassi ?: "",
                socPercentage = car.soc?.toString() ?: "",
                frontLeftPressure = car.DE?.toString() ?: "",
                frontRightPressure = car.DD?.toString() ?: "",
                rearLeftPressure = car.TE?.toString() ?: "",
                rearRightPressure = car.TD?.toString() ?: "",
                pdiId = car.pdiId
            )
        }

        Timber.d("Initialized form with car: ${car.name}, chassi: ${car.chassi}")
    }

    /**
     * Submit the PDI form
     */
    private val TAG = "PDISubmission"

    private fun submitPdi(
        context: Context,
        userId: Long,
        dealerCode: String,
        modelId: Int? = null,
        isCorrection: Boolean = false
    ) {
        // First validate the form
        if (!validateForm(requireBattery12V = _state.value.batteryVoltage.isNotBlank())) {
            return
        }

        // Start submission process
        _submissionState.value = SubmissionState.Submitting

        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting PDI submission process. isCorrection: $isCorrection")

                if (isCorrection) {
                    // Update existing PDI
                    val pdiId = _state.value.pdiId ?: return@launch

                    Log.d(TAG, "Updating existing PDI ID: $pdiId")

                    // Get deleted image IDs
                    val deletedImageIds = getDeletedImageIds()

                    // Update the PDI data
                    val updateResult = repository.updatePdi(
                        pdiId = pdiId,
                        state = _state.value,
                        context = context,
                        userId = userId,
                        deletedImageIds = deletedImageIds // Pass deleted image IDs
                    )

                    if (updateResult.isSuccess) {
                        // Upload new images
                        repository.uploadPdiImages(pdiId, _state.value, context)

                        _submissionState.value = SubmissionState.Success
                        showSuccessDialog()
                    } else {
                        Log.e(TAG, "Failed to update PDI: ${updateResult.exceptionOrNull()?.message}")
                        _submissionState.value = SubmissionState.Error(
                            updateResult.exceptionOrNull()?.message ?: "Failed to update PDI"
                        )
                    }
                } else {
                    // Create new PDI
                    Log.d(TAG, "Creating new PDI")

                    // Submit the new PDI
                    val submitResult = repository.submitNewPdi(
                        state = _state.value,
                        context = context,
                        userId = userId,
                        dealerCode = dealerCode,
                        modelId = modelId
                    )

                    if (submitResult.isSuccess) {
                        // Upload images for the new PDI
                        val pdiId = submitResult.getOrNull() ?: throw Exception("PDI ID not returned")

                        // Store the PDI ID in state
                        _state.update { it.copy(pdiId = pdiId) }

                        // Upload the images
                        Log.d(TAG, "Uploading images for new PDI ID: $pdiId")
                        repository.uploadPdiImages(pdiId, _state.value, context)

                        _submissionState.value = SubmissionState.Success
                        showSuccessDialog()
                    } else {
                        Log.e(TAG, "Failed to create PDI: ${submitResult.exceptionOrNull()?.message}")
                        _submissionState.value = SubmissionState.Error(
                            submitResult.exceptionOrNull()?.message ?: "Failed to create PDI"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error submitting PDI", e)
                _submissionState.value = SubmissionState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Submission state sealed class
     */
    sealed class SubmissionState {
        object Idle : SubmissionState()
        object Submitting : SubmissionState()
        object Success : SubmissionState()
        data class Error(val message: String) : SubmissionState()
    }
}