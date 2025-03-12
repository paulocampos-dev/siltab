package com.prototype.silver_tab.utils

import com.prototype.silver_tab.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getModelIdFromName(carName: String): Int? {
    val normalizedName = carName.trim().uppercase()
    return when (normalizedName) {
        "BYD YUAN PLUS" -> 1
        "BYD TAN" -> 2
        "BYD YUAN PRO" -> 3
        "BYD SEAL" -> 4
        "BYD HAN" -> 5
        "BYD DOLPHIN PLUS" -> 6
        "BYD DOLPHIN" -> 7
        "BYD DOLPHIN MINI" -> 8
        "BYD SONG PRO DM-I" -> 9
        "SONG PLUS PREMIUM DM-I" -> 10
        "BYD SONG PLUS DM-I" -> 11
        "BYD KING DM-I" -> 12
        "BYD SHARK" -> 13
        else -> null
    }
}

