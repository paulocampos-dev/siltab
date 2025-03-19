package com.prototype.silver_tab.data.models

import android.net.Uri
import com.prototype.silver_tab.R



/**
 * Data class that represents combined information about a car inspection
 */
data class InspectionInfo(
    // Car identification
    val carId: Int? = null,
    val vin: String? = null,
    val name: String,
    val type: String? = null,  // Vehicle type (e.g., "hybrid", "electric")

    // PDI information
    val pdiId: Int? = null,
    val date: String? = null,  // PDI creation or last modified date
    val createdBy: String? = null,
    val lastModifiedBy: String? = null,

    // Inspection data
    val soc: Float? = null,  // State of charge percentage
    val battery12v: Float? = null,  // 12V battery voltage

    // Tire pressures (PSI)
    val frontLeftTire: Float? = null,
    val frontRightTire: Float? = null,
    val rearLeftTire: Float? = null,
    val rearRightTire: Float? = null,

    // Additional information
    val comments: String? = null,
    val isComplete: Boolean? = false, // Whether inspection is complete
    val dealerCode: String? = null,

    // For new records
    val isNew: Boolean = false,

    // For sold cars
    val isSold: Boolean = false,
    val soldDate: String? = null,

    // For correction mode
    val isCorrection: Boolean = false
)