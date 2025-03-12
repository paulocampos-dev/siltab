package com.prototype.silver_tab.utils

fun determineCarTypeFromModel(model: String): String {

    // Map of car models to their types
    val carTypes = mapOf(
        "BYD YUAN PLUS" to "Elétrico",
        "BYD TAN" to "Híbrido",
        "BYD YUAN PRO" to "Elétrico",
        "BYD SEAL" to "Elétrico",
        "BYD HAN" to "Elétrico",
        "BYD DOLPHIN PLUS" to "Elétrico",
        "BYD DOLPHIN" to "Elétrico",
        "BYD DOLPHIN MINI" to "Elétrico",
        "BYD SONG PRO DM-i" to "Híbrido",
        "SONG PLUS PREMIUM DM-i" to "Híbrido",
        "BYD SONG PLUS DM-i" to "Híbrido",
        "BYD KING DM-i" to "Híbrido",
        "BYD SHARK" to "Híbrido"
    )

    return carTypes[model] ?: "Unknown"
}