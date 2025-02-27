package com.prototype.silver_tab.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.api.AuthManager
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
                // Verify token is available
                val token = AuthManager.getAccessToken()
                if (token.isNullOrEmpty()) {
                    _dealerState.value = DealerState.Error("No authentication token available")
                    return@launch
                }

                val response = RetrofitClient.dealerApi.getDealerSummary()
                _dealerState.value = DealerState.Success(response)
                if (response.size == 1) {
                    _selectedDealer.value = response.first()
                    Log.d("DealerViewModel", "Selecionado automaticamente: ${response.first().dealerCode}")
                }
            } catch (e: Exception) {
                Log.e("DealerViewModel", "Error loading dealers", e)
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