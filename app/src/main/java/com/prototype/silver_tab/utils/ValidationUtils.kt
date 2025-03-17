package com.prototype.silver_tab.utils

// Validation result sealed class
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

// Validation functions
object Validators {
    // VIN validation
    fun validateVin(vin: String): ValidationResult {
        return when {
            vin.isBlank() -> ValidationResult.Error("VIN is required")
            vin.length != 17 -> ValidationResult.Error("VIN must be 17 characters")
            !vin.matches("[A-HJ-NPR-Z0-9]{17}".toRegex()) -> ValidationResult.Error("Invalid VIN format")
            else -> ValidationResult.Valid
        }
    }

    // SOC validation
    fun validateSoc(soc: String): ValidationResult {
        return try {
            when {
                soc.isBlank() -> ValidationResult.Error("SOC % is required")
                soc.toFloat() < 0 -> ValidationResult.Error("SOC must be positive")
                soc.toFloat() > 100 -> ValidationResult.Error("SOC cannot exceed 100%")
                else -> ValidationResult.Valid
            }
        } catch (e: NumberFormatException) {
            ValidationResult.Error("Invalid number format")
        }
    }

    // Tire pressure validation
    fun validateTirePressure(pressure: String): ValidationResult {
        return try {
            when {
                pressure.isBlank() -> ValidationResult.Error("Tire pressure is required")
                pressure.toFloat() < 0 -> ValidationResult.Error("Pressure must be positive")
                pressure.toFloat() > 50 -> ValidationResult.Error("Pressure cannot exceed 50 PSI")
                else -> ValidationResult.Valid
            }
        } catch (e: NumberFormatException) {
            ValidationResult.Error("Invalid number format")
        }
    }

    // Battery voltage validation
    fun validateBatteryVoltage(voltage: String): ValidationResult {
        return try {
            when {
                voltage.isBlank() -> ValidationResult.Error("Battery voltage is required")
                voltage.toFloat() < 0 -> ValidationResult.Error("Voltage must be positive")
                voltage.toFloat() > 12 -> ValidationResult.Error("Voltage cannot exceed 12V")
                else -> ValidationResult.Valid
            }
        } catch (e: NumberFormatException) {
            ValidationResult.Error("Invalid number format")
        }
    }

    // Helper to format numbers to 2 decimal places
    fun formatToTwoDecimalPlaces(value: String): String {
        return try {
            val floatValue = value.toFloat()
            String.format("%.2f", floatValue)
        } catch (e: NumberFormatException) {
            value
        }
    }
}