package com.prototype.silver_tab.session

import com.prototype.silver_tab.data.models.DealerSummary
import com.prototype.silver_tab.data.models.InspectionInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/*

Shared Session State across application

*/

@Singleton
class AppSessionManager @Inject constructor() {

    // Dealer State
    private val _selectedDealer = MutableStateFlow<DealerSummary?>(null)
    val selectedDealer: StateFlow<DealerSummary?> = _selectedDealer.asStateFlow()

    // Selected Inspection
    private val _selectedInspection = MutableStateFlow<InspectionInfo?>(null)
    val selectedInspection: StateFlow<InspectionInfo?> = _selectedInspection.asStateFlow()

    // Methods
    fun selectDealer(dealer: DealerSummary) {
        _selectedDealer.value = dealer
    }

    fun selectInspection(inspection: InspectionInfo) {
        _selectedInspection.value = inspection
    }

    // Clear session (e.g., on logout)
    fun clearSession() {
        _selectedDealer.value = null
        _selectedInspection.value = null
    }
}