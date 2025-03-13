package com.prototype.silver_tab.data.repository

import com.prototype.silver_tab.data.api_connection.routes.DealerApi
import com.prototype.silver_tab.ui.components.DealerState
import com.prototype.silver_tab.ui.components.DealerSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that manages dealer data and selection state.
 * Using Singleton scope to ensure a single instance is shared across the app.
 */
@Singleton
class DealerRepository @Inject constructor(
    private val dealerApi: DealerApi
) {
    // StateFlow to hold dealer loading state
    private val _dealerState = MutableStateFlow<DealerState>(DealerState.Loading)
    val dealerState: StateFlow<DealerState> = _dealerState.asStateFlow()

    // StateFlow to hold the currently selected dealer
    private val _selectedDealer = MutableStateFlow<DealerSummary?>(null)
    val selectedDealer: StateFlow<DealerSummary?> = _selectedDealer.asStateFlow()

    /**
     * Loads dealer data from the API
     */
    suspend fun loadDealers() {
        Timber.d("Loading dealers")
        _dealerState.value = DealerState.Loading
        try {
            val response = dealerApi.getDealerSummary()
            _dealerState.value = DealerState.Success(response)

            if (response.size == 1) {
                _selectedDealer.value = response.first()
                Timber.d("Automatically selected dealer: ${response.first().dealerCode}")
            } else {
                Timber.d("Multiple dealers found: ${response.size}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading dealers")
            _dealerState.value = DealerState.Error("Error loading dealers: ${e.message}")
        }
    }

    /**
     * Updates the selected dealer
     */
    fun selectDealer(dealer: DealerSummary) {
        Timber.d("Dealer selected: ${dealer.dealerCode}")
        _selectedDealer.value = dealer
    }

    /**
     * Clears the dealer state
     */
    fun clearDealerState() {
        Timber.d("Clearing dealer state")
        _dealerState.value = DealerState.Loading
        _selectedDealer.value = null
    }
}