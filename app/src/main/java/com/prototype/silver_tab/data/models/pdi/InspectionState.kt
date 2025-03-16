package com.prototype.silver_tab.data.models.pdi

sealed class InspectionState {
    object Loading : InspectionState()
    data class Success(val inspections: List<PDI>) : InspectionState()
    data class Error(val message: String) : InspectionState()
}