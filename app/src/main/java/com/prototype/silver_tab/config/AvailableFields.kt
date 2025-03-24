package com.prototype.silver_tab.config


enum class FieldType {
    VIN,
    // VIN PHOTOS HERE
    SOC,
    BATTERY_12V,
    TIRE_PRESSURE,
    HYBRID_CHECK,
    COMMENTS,
    EXTRA_PHOTOS
}


object AvailableFields {
    // Default configuration - only VIN, SOC, and Comments are enabled
    private var enabledFields: Set<FieldType> = setOf(
        FieldType.VIN,
        FieldType.SOC,
        FieldType.COMMENTS,
        FieldType.EXTRA_PHOTOS
    )

    fun isFieldEnabled(fieldType: FieldType): Boolean {
        return enabledFields.contains(fieldType)
    }

    fun updateEnabledFields(fields: Set<FieldType>) {
        enabledFields = fields
    }


    fun getEnabledFields(): Set<FieldType> {
        return enabledFields
    }
}