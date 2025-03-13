package com.prototype.silver_tab.data.repository

import com.prototype.silver_tab.ui.components.DealerSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for sharing dealer selection state across multiple ViewModels.
 */
@Singleton
class DealerSelectionRepository @Inject constructor() {
    private val _selectedDealer = MutableStateFlow<DealerSummary?>(null)
    val selectedDealer: StateFlow<DealerSummary?> = _selectedDealer.asStateFlow()

    fun selectDealer(dealer: DealerSummary) {
        Timber.d("DealerSelectionRepository: Updating selected dealer to: ${dealer.dealerCode}")
        _selectedDealer.value = dealer
    }

    fun clearSelectedDealer() {
        Timber.d("DealerSelectionRepository: Clearing selected dealer")
        _selectedDealer.value = null
    }
}