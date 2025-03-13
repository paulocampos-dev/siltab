package com.prototype.silver_tab.data.models

import android.net.Uri
import com.prototype.silver_tab.R
import kotlinx.serialization.Serializable


@Serializable
data class InspectionInfo(
    val name: String? = null,
    val pdiId: Int? = null,
    val chassi: String? = null,
    val type: String? = null,
    val date: String? = null,
    val image: Int? = null,
    val route: String? =  null,
    val soc: Float? = null,
    val DE: Float? = null,
    val DD: Float? = null,
    val TD: Float? = null,
    val TE: Float? = null,

    @Transient
    val preloadedImages: Map<String, List<InspectionImage>>? = null
)

/**
 * Represents an image associated with a PDI inspection
 */
data class InspectionImage(
    val imageId: Int,
    val imageType: String,
    val uri: Uri
)

val BydInspectionInfos = listOf(
    InspectionInfo(
        name = "BYD SHARK",
        type = "Híbrido",
        image = R.drawable.byd_shark,
    ),
    InspectionInfo(
        name = "BYD KING DM-i",
        type = "Híbrido",
        image = R.drawable.byd_king,
    ),
    InspectionInfo(
        name = "BYD SONG PLUS DM-i",
        type = "Híbrido",
        image = R.drawable.byd_song_plus,
    ),
    InspectionInfo(
        name = "SONG PLUS PREMIUM DM-i",
        type = "Híbrido",
        //image = R.drawable.song_plus_premium,
        image = R.drawable.byd_song_premium,
    ),
    InspectionInfo(
        name = "BYD SONG PRO DM-i",
        type = "Híbrido",
        image = R.drawable.byd_song_pro,
    ),
    InspectionInfo(
        name = "BYD DOLPHIN MINI",
        type = "Elétrico",
        image = R.drawable.byd_dolphin_mini,
    ),
    InspectionInfo(
        name = "BYD DOLPHIN",
        type = "Elétrico",
        image = R.drawable.byd_dolphin,
    ),
    InspectionInfo(
        name = "BYD DOLPHIN PLUS",
        type = "Elétrico",
        image = R.drawable.byd_dolphin_plus,
    ),
    InspectionInfo(
        name = "BYD HAN",
        type = "Elétrico",
        image = R.drawable.byd_han,
    ),
    InspectionInfo(
        name = "BYD SEAL",
        type = "Elétrico",
        image = R.drawable.pid_car,
    ),
    InspectionInfo(
        name = "BYD TAN",
        type = "Híbrido",
        image = R.drawable.byd_tan,
    ),
    InspectionInfo(
        name = "BYD YUAN PLUS",
        type = "Elétrico",
        image = R.drawable.byd_yuan_plus,
    ),
    InspectionInfo(
        name = "BYD YUAN PRO",
        type = "Elétrico",
        image = R.drawable.byd_yuan_pro,
    )
)
