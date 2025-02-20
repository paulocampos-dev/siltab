package com.prototype.silver_tab.viewmodels

import com.prototype.silver_tab.data.mappers.CarsData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.api.RetrofitClient
import kotlinx.coroutines.launch

class CarsDataViewModel (  private val dealerViewModel: DealerViewModel
): ViewModel() {
    private val _carsState = MutableLiveData<CarsState>(CarsState.Loading)
    val carsState: LiveData<CarsState> = _carsState


    init {
        observeDealerCode()
    }
    private fun observeDealerCode() {
        viewModelScope.launch {
            dealerViewModel.selectedDealer.collect { dealer ->
                dealer?.let {
                    loadData(it.dealerCode)  // Passando o código atualizado do dealer
                }
            }
        }
    }

    fun loadData(dealerCode: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.carsApi.getCarsDealer(dealerCode)
                _carsState.value = CarsState.Success(CarsData(response))

            } catch (e: Exception) {
                _carsState.value = CarsState.Error("Erro ao carregar: ${e.message}")
            }
        }
    }
}

sealed class CarsState {
    object Loading : CarsState()
    data class Success(val data: List<Map<String, String?>>) : CarsState()
    data class Error(val message: String) : CarsState()
}
