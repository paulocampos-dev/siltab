package com.prototype.silver_tab.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.data.api_connection.RetrofitClient
import com.prototype.silver_tab.ui.components.DealerState
import com.prototype.silver_tab.ui.components.DealerSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DealerViewModel : ViewModel() {
    private val _dealerState = MutableStateFlow<DealerState>(DealerState.Loading)
    val dealerState: StateFlow<DealerState> = _dealerState.asStateFlow()

    private val _selectedDealer = MutableStateFlow<DealerSummary?>(null)
    val selectedDealer: StateFlow<DealerSummary?> = _selectedDealer.asStateFlow()

    private val authRepository = SilverTabApplication.authRepository

    init {
        viewModelScope.launch {
            // Observe auth state changes
            authRepository.authState.collectLatest { state ->
                if (state.isAuthenticated) {
                    loadDealers()
                } else {
                    // Clear dealer state when logged out
                    _dealerState.value = DealerState.Loading
                    _selectedDealer.value = null
                }
            }
        }
    }

    private fun loadDealers() {
        viewModelScope.launch {
            _dealerState.value = DealerState.Loading
            try {
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

    fun notifyAuthenticated() {
        // This method is now just a trigger to force reload dealers
        loadDealers()
    }

    fun selectDealer(dealer: DealerSummary) {
        _selectedDealer.value = dealer
    }

    fun refreshDealers() {
        loadDealers()
    }
}