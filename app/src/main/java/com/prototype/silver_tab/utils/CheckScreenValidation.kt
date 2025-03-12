package com.prototype.silver_tab.utils

import androidx.compose.runtime.MutableState
import com.prototype.silver_tab.viewmodels.CheckScreenState

/**
 * Helper class containing validation functions for the Check Screen form
 */
class CheckScreenValidation {
    companion object {
        /**
         * Validates that the chassis number is not blank
         */
        fun validateChassisNumber(value: String, error: MutableState<Boolean>): Boolean {
            val isValid = value.isNotBlank() // && value.length == 17 && isValidVIN(value)
            error.value = !isValid
            return isValid
        }

        /**
         * Validates SOC percentage (must be between 0 and 100, with at most 2 decimal places)
         */
        fun validateSocPercentage(value: String, error: MutableState<Boolean>): Boolean {
            if (value.isBlank()) {
                error.value = true
                return false
            }

            val processedValue = value.replace(',', '.').filter { it.isDigit() || it == '.' }
            val numericValue = processedValue.toDoubleOrNull()
            val isValid = numericValue != null && numericValue <= 100.0

            error.value = !isValid
            return isValid
        }

        /**
         * Validates tire pressure (must be less than 50 PSI)
         */
        fun validateTirePressure(value: String, error: MutableState<Boolean>): Boolean {
            if (value.isBlank()) {
                error.value = true
                return false
            }

            val processedValue = value.replace(',', '.').filter { it.isDigit() || it == '.' }
            val numericValue = processedValue.toDoubleOrNull()
            val isValid = numericValue != null && numericValue < 50.0

            error.value = !isValid
            return isValid
        }

        /**
         * Validates battery voltage (must be less than 12V for 12V batteries)
         */
        fun validateBatteryVoltage(value: String, error: MutableState<Boolean>): Boolean {
            if (value.isBlank()) {
                error.value = true
                return false
            }

            val processedValue = value.replace(',', '.').filter { it.isDigit() || it == '.' }
            val numericValue = processedValue.toDoubleOrNull()
            val isValid = numericValue != null && numericValue <= 12.0

            error.value = !isValid
            return isValid
        }

        /**
         * Validates the entire form, checking all required fields
         */
        fun validateForm(
            state: CheckScreenState,
            requireBattery12V: Boolean,
            chassisError: MutableState<Boolean>,
            socError: MutableState<Boolean>,
            frontLeftError: MutableState<Boolean>,
            frontRightError: MutableState<Boolean>,
            rearLeftError: MutableState<Boolean>,
            rearRightError: MutableState<Boolean>,
            batteryVoltageError: MutableState<Boolean>
        ): Boolean {
            var isValid = true

            // Validate each field
            if (!validateChassisNumber(state.chassisNumber, chassisError)) {
                isValid = false
            }

            if (!validateSocPercentage(state.socPercentage, socError)) {
                isValid = false
            }

            if (!validateTirePressure(state.frontLeftPressure, frontLeftError)) {
                isValid = false
            }

            if (!validateTirePressure(state.frontRightPressure, frontRightError)) {
                isValid = false
            }

            if (!validateTirePressure(state.rearLeftPressure, rearLeftError)) {
                isValid = false
            }

            if (!validateTirePressure(state.rearRightPressure, rearRightError)) {
                isValid = false
            }

            if (requireBattery12V && !validateBatteryVoltage(state.batteryVoltage, batteryVoltageError)) {
                isValid = false
            }

            return isValid
        }

        /**
         * Process a numeric input to ensure proper decimal format
         */
        fun processNumericInput(newValue: String): String {
            if (newValue.isBlank()) return newValue

            // Replace comma with period and filter non-numeric characters
            val processedValue = newValue.replace(',', '.').filter { it.isDigit() || it == '.' }

            // Ensure there's only one decimal point
            val validatedValue = if (processedValue.count { it == '.' } > 1) {
                val firstDotIndex = processedValue.indexOf('.')
                processedValue.substring(0, firstDotIndex + 1) +
                        processedValue.substring(firstDotIndex + 1).replace(".", "")
            } else {
                processedValue
            }

            // Ensure at most two decimal places
            return validatedValue.split('.').let { parts ->
                if (parts.size == 2 && parts[1].length > 2) {
                    "${parts[0]}.${parts[1].take(2)}"
                } else {
                    validatedValue
                }
            }
        }
    }
}