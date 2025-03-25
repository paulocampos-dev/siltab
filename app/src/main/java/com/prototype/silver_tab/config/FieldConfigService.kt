package com.prototype.silver_tab.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError

/**
 * Data model for server field configuration response
 */
data class FieldConfigResponse(
    val fields: List<String> // Field names as strings (will be converted to FieldType enum)
)

/**
 * Service to fetch and manage field configuration from server
 * This is a skeleton implementation for future development
 */
@Singleton
class FieldConfigService @Inject constructor() {
    private val tag = "FieldConfigService"

    // Configuration state that can be observed by ViewModels
    private val _configState = MutableStateFlow<FieldConfigState>(FieldConfigState.Loading)
    val configState: StateFlow<FieldConfigState> = _configState.asStateFlow()

    /**
     * Fetch field configuration from server
     * This is a placeholder for future implementation
     */
    suspend fun fetchFieldConfig() {
        _configState.value = FieldConfigState.Loading

        try {
            // In the future, this would be a real API call
            // val response = fieldConfigApi.getFieldConfig()

            // Simulate a successful response for now
            val simulatedFields = setOf(
                FieldType.VIN,
                FieldType.SOC,
                FieldType.COMMENTS,
            )

            // Update the global AvailableFields configuration
            AvailableFields.updateEnabledFields(simulatedFields)

            _configState.value = FieldConfigState.Success(simulatedFields)
            logTimber(tag, "Loaded field configuration with ${simulatedFields.size} enabled fields")
        } catch (e: Exception) {
            logTimberError(tag, "Failed to load field configuration: ${e.message}")
            _configState.value = FieldConfigState.Error("Failed to load field configuration: ${e.message}")
        }
    }

    /**
     * Reset to default configuration
     */
    fun resetToDefault() {
        val defaultFields = setOf(
            FieldType.VIN,
            FieldType.SOC,
            FieldType.COMMENTS,
        )

        AvailableFields.updateEnabledFields(defaultFields)
        _configState.value = FieldConfigState.Success(defaultFields)
        logTimber(tag, "Reset to default field configuration")
    }
}

/**
 * Sealed class representing field configuration state
 */
sealed class FieldConfigState {
    object Loading : FieldConfigState()
    data class Success(val enabledFields: Set<FieldType>) : FieldConfigState()
    data class Error(val message: String) : FieldConfigState()
}