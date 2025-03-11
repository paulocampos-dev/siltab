package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.api_connection.RetrofitClient
import com.prototype.silver_tab.data.mappers.PdiData
import kotlinx.coroutines.launch

class PdiDataViewModel(
    private val dealerViewModel: DealerViewModel
) : ViewModel() {
    private val _pdiState = MutableLiveData<PdiState>(PdiState.Loading)
    val pdiState: LiveData<PdiState> = _pdiState

    init {
        observeDealerCode()
    }

    private fun observeDealerCode() {
        viewModelScope.launch {
            dealerViewModel.selectedDealer.collect { dealer ->
                dealer?.let { loadData(it.dealerCode) }
            }
        }
    }

    fun loadData(dealerCode: String) {
        
        if (dealerCode.isBlank()) return

        _pdiState.value = PdiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.pdiApi.getPdi(dealerCode)
                _pdiState.postValue(PdiState.Success(PdiData(response)))
            } catch (e: Exception) {
                _pdiState.postValue(PdiState.Error("Erro ao carregar: ${e.message}"))
            }
        }
    }
}



sealed class PdiState {
    object Loading : PdiState()
    data class Success(val data: List<Map<String, String?>>) : PdiState()
    data class Error(val message: String) : PdiState()
}

