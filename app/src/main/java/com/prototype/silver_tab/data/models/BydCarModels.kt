package com.prototype.silver_tab.data.models

/**
 * Data class to represent a BYD car model
 */
data class BydCarModel(
    val id: Int,
    val name: String,
    val type: String, // "Electric" or "Hybrid"
    val imageResId: Int? = null // Resource ID for car image
)

/**
 * List of all available BYD car models
 */
object BydCarModels {
    val modelsList = listOf(
        BydCarModel(1, "BYD YUAN PLUS", "Electric"),
        BydCarModel(2, "BYD TAN", "Hybrid"),
        BydCarModel(3, "BYD YUAN PRO", "Electric"),
        BydCarModel(4, "BYD SEAL", "Electric"),
        BydCarModel(5, "BYD HAN", "Electric"),
        BydCarModel(6, "BYD DOLPHIN PLUS", "Electric"),
        BydCarModel(7, "BYD DOLPHIN", "Electric"),
        BydCarModel(8, "BYD DOLPHIN MINI", "Electric"),
        BydCarModel(9, "BYD SONG PRO DM-I", "Hybrid"),
        BydCarModel(10, "SONG PLUS PREMIUM DM-I", "Hybrid"),
        BydCarModel(11, "BYD SONG PLUS DM-I", "Hybrid"),
        BydCarModel(12, "BYD KING DM-I", "Hybrid"),
        BydCarModel(13, "BYD SHARK", "Hybrid")
    )

    // Get car model by ID
    fun getModelById(id: Int): BydCarModel? {
        return modelsList.find { it.id == id }
    }

    // Get car model by name
    fun getModelByName(name: String): BydCarModel? {
        val normalizedName = name.trim().uppercase()
        return modelsList.find { it.name.equals(normalizedName, ignoreCase = true) }
    }
}