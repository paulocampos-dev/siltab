package com.prototype.silver_tab.utils.validation

import com.prototype.silver_tab.utils.isValidVIN
import com.prototype.silver_tab.viewmodels.CheckScreenState

/**
 * Represents the result of a validation check
 */
sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

/**
 * Utility class to handle validation logic for the Check Screen form
 */
class CheckScreenValidator {

    companion object {
        /**
         * Validates a chassis/VIN number
         * @param value The VIN to validate
         * @param validateFormat Whether to validate the VIN format (length & check digit)
         * @return Validation result
         */
        fun validateChassisNumber(value: String, validateFormat: Boolean = true): ValidationResult {
            if (value.isBlank()) {
                return ValidationResult.Error("Chassis number cannot be empty")
            }

            if (validateFormat) {
                if (value.length != 17) {
                    return ValidationResult.Error("VIN must be 17 characters long")
                }

                if (!isValidVIN(value)) {
                    return ValidationResult.Error("Invalid VIN format or check digit")
                }
            }

            return ValidationResult.Success
        }

        /**
         * Validates SOC percentage value
         * @param value The SOC percentage value to validate
         * @return Validation result
         */
        fun validateSocPercentage(value: String): ValidationResult {
            if (value.isBlank()) {
                return ValidationResult.Error("SOC percentage cannot be empty")
            }

            val numericValue = value.replace(',', '.').toDoubleOrNull()
            if (numericValue == null) {
                return ValidationResult.Error("SOC percentage must be a valid number")
            }

            if (numericValue < 0 || numericValue > 100) {
                return ValidationResult.Error("SOC percentage must be between 0 and 100")
            }

            // Check for decimal places (max 2)
            val parts = value.replace(',', '.').split('.')
            if (parts.size > 1 && parts[1].length > 2) {
                return ValidationResult.Error("SOC percentage can have at most 2 decimal places")
            }

            return ValidationResult.Success
        }

        /**
         * Validates tire pressure value
         * @param value The tire pressure value to validate
         * @return Validation result
         */
        fun validateTirePressure(value: String): ValidationResult {
            if (value.isBlank()) {
                return ValidationResult.Error("Tire pressure cannot be empty")
            }

            val numericValue = value.replace(',', '.').toDoubleOrNull()
            if (numericValue == null) {
                return ValidationResult.Error("Tire pressure must be a valid number")
            }

            if (numericValue < 0 || numericValue >= 50) {
                return ValidationResult.Error("Tire pressure must be between 0 and 50")
            }

            return ValidationResult.Success
        }

        /**
         * Validates 12V battery voltage
         * @param value The battery voltage value to validate
         * @return Validation result
         */
        fun validateBatteryVoltage(value: String): ValidationResult {
            if (value.isBlank()) {
                return ValidationResult.Error("Battery voltage cannot be empty")
            }

            val numericValue = value.replace(',', '.').toDoubleOrNull()
            if (numericValue == null) {
                return ValidationResult.Error("Battery voltage must be a valid number")
            }

            if (numericValue < 0 || numericValue > 15) {
                return ValidationResult.Error("Battery voltage must be between 0 and 15V")
            }

            return ValidationResult.Success
        }

        /**
         * Format numeric input for consistent representation
         * @param input The input string
         * @return Formatted string
         */
        fun formatNumericInput(input: String): String {
            if (input.isBlank()) return input

            // Replace comma with period
            val withPeriod = input.replace(',', '.')

            // Ensure there's only one decimal point
            val parts = withPeriod.split('.')
            return if (parts.size > 1) {
                "${parts[0]}.${parts.drop(1).joinToString("")}"
            } else {
                withPeriod
            }
        }

        /**
         * Validates the entire form
         * @param state The current form state
         * @param requireBattery12V Whether 12V battery voltage is required
         * @return Map of field names to validation results
         */
        fun validateForm(state: CheckScreenState, requireBattery12V: Boolean): Map<String, ValidationResult> {
            val validationResults = mutableMapOf<String, ValidationResult>()

            validationResults["chassisNumber"] = validateChassisNumber(state.chassisNumber)
            validationResults["socPercentage"] = validateSocPercentage(state.socPercentage)
            validationResults["frontLeftPressure"] = validateTirePressure(state.frontLeftPressure)
            validationResults["frontRightPressure"] = validateTirePressure(state.frontRightPressure)
            validationResults["rearLeftPressure"] = validateTirePressure(state.rearLeftPressure)
            validationResults["rearRightPressure"] = validateTirePressure(state.rearRightPressure)

            if (requireBattery12V) {
                validationResults["batteryVoltage"] = validateBatteryVoltage(state.batteryVoltage)
            }

            return validationResults
        }

        /**
         * Checks if the form is valid
         * @param validationResults The map of validation results
         * @return True if all fields are valid, false otherwise
         */
        fun isFormValid(validationResults: Map<String, ValidationResult>): Boolean {
            return validationResults.values.all { it is ValidationResult.Success }
        }
    }
}