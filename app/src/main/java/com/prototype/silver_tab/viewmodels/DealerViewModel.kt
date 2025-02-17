package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.api.RetrofitClient
import com.prototype.silver_tab.ui.components.DealerState
import com.prototype.silver_tab.ui.components.DealerSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DealerViewModel : ViewModel() {
    private val _dealerState = MutableStateFlow<DealerState>(DealerState.Loading)
    val dealerState: StateFlow<DealerState> = _dealerState.asStateFlow()

    private val _selectedDealer = MutableStateFlow<DealerSummary?>(null)
    val selectedDealer: StateFlow<DealerSummary?> = _selectedDealer.asStateFlow()

    init {
        loadDealers()
    }

    private fun loadDealers() {
        viewModelScope.launch {
            _dealerState.value = DealerState.Loading
            try {
                val response = RetrofitClient.dealerApi.getDealerSummary()
                _dealerState.value = DealerState.Success(response)
            } catch (e: Exception) {
                _dealerState.value = DealerState.Error("Error loading dealers: ${e.message}")
            }
        }
    }

    fun selectDealer(dealer: DealerSummary) {
        _selectedDealer.value = dealer
    }

    fun refreshDealers() {
        loadDealers()
    }
}