package com.prototype.silver_tab.data.repository

import com.prototype.silver_tab.data.routes.DealerApi
import com.prototype.silver_tab.data.models.DealerState
import com.prototype.silver_tab.data.models.DealerSummary
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.utils.logTimberError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DealerRepository @Inject constructor(
    private val dealerApi: DealerApi
) {
    val tag = "DealerRepository"

    // StateFlow to hold dealer loading state
    private val _dealerState = MutableStateFlow<DealerState>(DealerState.Loading)
    val dealerState: StateFlow<DealerState> = _dealerState.asStateFlow()

    // StateFlow to hold the currently selected dealer
    private val _selectedDealer = MutableStateFlow<DealerSummary?>(null)
    val selectedDealer: StateFlow<DealerSummary?> = _selectedDealer.asStateFlow()

    private val _possibleDealers = MutableStateFlow<List<DealerSummary>>(emptyList())
    val possibleDealers: StateFlow<List<DealerSummary>> = _possibleDealers.asStateFlow()

    suspend fun loadDealers() {
        logTimber(tag, "Loading Dealers")
        _dealerState.value = DealerState.Loading

        try {
            val response = dealerApi.getDealerSummary()
            logTimber(tag, "Dealer API response: ${response.code()}")

            if (response.isSuccessful) {
                val dealers = response.body() ?: emptyList()
                logTimber(tag, "DealerRepository: Loaded ${dealers.size} dealers")
                _dealerState.value = DealerState.Success(dealers)

                if (dealers.size == 1 && _selectedDealer.value == null) {
                    logTimber(tag, "DealerRepository: Auto-selecting single dealer: ${dealers.first().dealerCode}")
                    selectDealer(dealers.first())
                }
            } else {
                logTimberError(tag, "DealerRepository: Failed to load dealers: ${response.code()} - ${response.message()}")
                _dealerState.value = DealerState.Error("Error loading dealers: ${response.code()}")
            }
        } catch (e: Exception) {
            logTimberError(tag, "DealerRepository: Exception loading dealers: ${e.message}")
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