package com.prototype.silver_tab.data.models

import android.net.Uri
import com.prototype.silver_tab.R


data class InspectionInfo(
    val pdiId: Int,
    val name: String,
    val vin: String,
    val type: String,
    val date: String,
    val soc: Float,
    val frontLeftTire: Float,
    val frontRightTire: Float,
    val rearRightTire: Float,
    val rearLeftTire: Float,
    val battery12v: Float? = null,
    val fiveMinutesCheck: Boolean? = null,
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
