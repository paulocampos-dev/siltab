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
    val isNew: Boolean = false
)



//val BydInspectionInfos = listOf(
//    InspectionInfo(
//        name = "BYD SHARK",
//        type = "Híbrido",
////        image = R.drawable.byd_shark,
//    ),
//    InspectionInfo(
//        name = "BYD KING DM-i",
//        type = "Híbrido",
////        image = R.drawable.byd_king,
//    ),
//    InspectionInfo(
//        name = "BYD SONG PLUS DM-i",
//        type = "Híbrido",
////        image = R.drawable.byd_song_plus,
//    ),
//    InspectionInfo(
//        name = "SONG PLUS PREMIUM DM-i",
//        type = "Híbrido",
////        image = R.drawable.byd_song_premium,
//    ),
//    InspectionInfo(
//        name = "BYD SONG PRO DM-i",
//        type = "Híbrido",
////        image = R.drawable.byd_song_pro,
//    ),
//    InspectionInfo(
//        name = "BYD DOLPHIN MINI",
//        type = "Elétrico",
////        image = R.drawable.byd_dolphin_mini,
//    ),
//    InspectionInfo(
//        name = "BYD DOLPHIN",
//        type = "Elétrico",
////        image = R.drawable.byd_dolphin,
//    ),
//    InspectionInfo(
//        name = "BYD DOLPHIN PLUS",
//        type = "Elétrico",
////        image = R.drawable.byd_dolphin_plus,
//    ),
//    InspectionInfo(
//        name = "BYD HAN",
//        type = "Elétrico",
////        image = R.drawable.byd_han,
//    ),
//    InspectionInfo(
//        name = "BYD SEAL",
//        type = "Elétrico",
////        image = R.drawable.pid_car,
//    ),
//    InspectionInfo(
//        name = "BYD TAN",
//        type = "Híbrido",
////        image = R.drawable.byd_tan,
//    ),
//    InspectionInfo(
//        name = "BYD YUAN PLUS",
//        type = "Elétrico",
////        image = R.drawable.byd_yuan_plus,
//    ),
//    InspectionInfo(
//        name = "BYD YUAN PRO",
//        type = "Elétrico",
////        image = R.drawable.byd_yuan_pro,
//    )
//)
