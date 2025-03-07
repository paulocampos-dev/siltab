package com.prototype.silver_tab.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.ui.components.checkscreen.ImageType
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

    // Image URIs - Updated to Lists
    val chassisImageUris: List<Uri> = emptyList(),
    val socImageUris: List<Uri> = emptyList(),
    val battery12VImageUris: List<Uri> = emptyList(),
    val tirePressureImageUris: List<Uri> = emptyList(),
    val extraImageUris: List<Uri> = emptyList(),

    // Dialog states
    val showCancelDialog: Boolean = false,
    val showFinishDialog: Boolean = false
)

class CheckScreenViewModel : ViewModel() {
    private val _state = MutableStateFlow(CheckScreenState())
    val state: StateFlow<CheckScreenState> = _state.asStateFlow()

    fun addImage(type: ImageType, uri: Uri) {
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
    }

    fun removeImage(type: ImageType, index: Int) {
        _state.update { currentState ->
            when (type) {
                ImageType.CHASSIS -> currentState.copy(
                    chassisImageUris = currentState.chassisImageUris.filterIndexed { i, _ -> i != index }
                )
                ImageType.SOC -> currentState.copy(
                    socImageUris = currentState.socImageUris.filterIndexed { i, _ -> i != index }
                )
                ImageType.BATTERY_12VOLTAGE -> currentState.copy(
                    battery12VImageUris = currentState.battery12VImageUris.filterIndexed { i, _ -> i != index }
                )
                ImageType.TIRE_PRESSURE -> currentState.copy(
                    tirePressureImageUris = currentState.tirePressureImageUris.filterIndexed { i, _ -> i != index }
                )
                ImageType.EXTRA_IMAGE -> currentState.copy(
                    extraImageUris = currentState.extraImageUris.filterIndexed { i, _ -> i != index }
                )
            }
        }
    }

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

    fun initializeWithCar(inspectionInfo: InspectionInfo) {
        _state.update { currentState ->
            currentState.copy(
                chassisNumber = inspectionInfo.chassi ?: "",
                socPercentage = inspectionInfo.soc?.toString() ?: "",
                frontLeftPressure = inspectionInfo.DE?.toString() ?: "",
                frontRightPressure = inspectionInfo.DD?.toString() ?: "",
                rearLeftPressure = inspectionInfo.TE?.toString() ?: "",
                rearRightPressure = inspectionInfo.TD?.toString() ?: ""
            )
        }
    }
}