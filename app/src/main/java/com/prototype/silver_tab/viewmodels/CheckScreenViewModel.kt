package com.prototype.silver_tab.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.prototype.silver_tab.ui.camera.ImageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CheckScreenState(
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

    // Image URIs
    val chassisImageUri: Uri? = null,
    val batteryImageUri: Uri? = null,
    val voltageImageUri: Uri? = null,
    val tirePressureImageUri: Uri? = null,
    val carStartedImageUri: Uri? = null,

    // Dialog states
    val showCancelDialog: Boolean = false,
    val showFinishDialog: Boolean = false
)

class CheckScreenViewModel : ViewModel() {
    private val _state = MutableStateFlow(CheckScreenState())
    val state: StateFlow<CheckScreenState> = _state.asStateFlow()

    // Text field updates
    fun updateChassisNumber(value: String) {
        _state.update { it.copy(chassisNumber = value) }
    }

    fun updateBatteryVoltage(value: String) {
        _state.update { it.copy(batteryVoltage = value) }
    }

    fun updateSocPercentage(value: String) {
        _state.update { it.copy(socPercentage = value) }
    }

    fun updateCarStarted(value: Boolean) {
        _state.update { it.copy(isCarStarted = value) }
    }

    fun updateAdditionalInfo(value: String) {
        _state.update { it.copy(additionalInfo = value) }
    }

    // Tire pressure updates
    fun updateFrontLeftPressure(value: String) {
        _state.update { it.copy(frontLeftPressure = value) }
    }

    fun updateFrontRightPressure(value: String) {
        _state.update { it.copy(frontRightPressure = value) }
    }

    fun updateRearLeftPressure(value: String) {
        _state.update { it.copy(rearLeftPressure = value) }
    }

    fun updateRearRightPressure(value: String) {
        _state.update { it.copy(rearRightPressure = value) }
    }

    // Image updates
    fun onImageCaptured(type: ImageType, uri: Uri) {
        when (type) {
            ImageType.CHASSIS -> _state.update { it.copy(chassisImageUri = uri) }
            ImageType.BATTERY -> _state.update { it.copy(batteryImageUri = uri) }
            ImageType.VOLTAGE -> _state.update { it.copy(voltageImageUri = uri) }
            ImageType.TIRE_PRESSURE -> _state.update { it.copy(tirePressureImageUri = uri) }
            ImageType.CAR_STARTED -> _state.update { it.copy(carStartedImageUri = uri) }
        }
    }

    // Dialog controls
    fun showCancelDialog() {
        _state.update { it.copy(showCancelDialog = true) }
    }

    fun hideCancelDialog() {
        _state.update { it.copy(showCancelDialog = false) }
    }

    fun showFinishDialog() {
        _state.update { it.copy(showFinishDialog = true) }
    }

    fun hideFinishDialog() {
        _state.update { it.copy(showFinishDialog = false) }
    }
}