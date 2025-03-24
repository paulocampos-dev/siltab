package com.prototype.silver_tab.config


enum class FieldType {
    VIN,
    SOC,
    BATTERY_12V,
    TIRE_PRESSURE,
    HYBRID_CHECK,
    COMMENTS,
}


object AvailableFields {
    // Default configuration - only VIN, SOC, and Comments are enabled
    private var enabledFields: Set<FieldType> = setOf(
        FieldType.VIN,
        FieldType.SOC,
        FieldType.COMMENTS,
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