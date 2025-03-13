package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.api_connection.routes.CarsApi
import com.prototype.silver_tab.data.mappers.CarsData
import com.prototype.silver_tab.data.repository.DealerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for loading car data.
 * Updated to use Hilt for dependency injection and DealerRepository.
 */
@HiltViewModel
class CarsDataViewModel @Inject constructor(
    private val dealerRepository: DealerRepository,
    private val carsApi: CarsApi
) : ViewModel() {
    private val _carsState = MutableLiveData<CarsState>(CarsState.Loading)
    val carsState: LiveData<CarsState> = _carsState

    init {
        Timber.d("CarsDataViewModel initialized with Hilt")
        observeDealerCode()
    }

    private fun observeDealerCode() {
        viewModelScope.launch {
            dealerRepository.selectedDealer.collect { dealer ->
                dealer?.let {
                    Timber.d("Dealer changed: ${it.dealerCode}, loading car data")
                    loadData(it.dealerCode)
                }
            }
        }
    }

    fun loadData(dealerCode: String) {
        if (dealerCode.isBlank()) {
            Timber.w("Empty dealer code provided, skipping data load")
            return
        }

        viewModelScope.launch {
            try {
                Timber.d("Loading cars data for dealer: $dealerCode")
                _carsState.value = CarsState.Loading
                val response = carsApi.getCarsDealer(dealerCode)
                val filteredResponse = response.filter { it.is_sold == false }

                _carsState.value = CarsState.Success(CarsData(filteredResponse))
                Timber.d("Successfully loaded ${filteredResponse.size} cars")
            } catch (e: Exception) {
                Timber.e(e, "Error loading cars data for dealer: $dealerCode")
                _carsState.value = CarsState.Error("Error loading cars data: ${e.message}")
            }
        }
    }
}

sealed class CarsState {
    object Loading : CarsState()
    data class Success(val data: List<Map<String, String?>>) : CarsState()
    data class Error(val message: String) : CarsState()
}