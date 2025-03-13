package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.api_connection.routes.PdiApi
import com.prototype.silver_tab.data.mappers.PdiData
import com.prototype.silver_tab.data.repository.DealerSelectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for loading PDI data.
 * Updated to use Hilt for dependency injection.
 */
@HiltViewModel
class PdiDataViewModel @Inject constructor(
    private val dealerSelectionRepository: DealerSelectionRepository,
    private val pdiApi: PdiApi
) : ViewModel() {
    private val _pdiState = MutableLiveData<PdiState>(PdiState.Loading)
    val pdiState: LiveData<PdiState> = _pdiState

    init {
        Timber.d("PdiDataViewModel initialized with Hilt")
        viewModelScope.launch {
            dealerSelectionRepository.selectedDealer.collect { dealer ->
                dealer?.let {
                    loadData(it.dealerCode)
                }
            }
        }
    }

    fun loadData(dealerCode: String) {
        if (dealerCode.isBlank()) {
            Timber.w("Empty dealer code provided, skipping PDI data load")
            return
        }

        _pdiState.value = PdiState.Loading
        viewModelScope.launch {
            try {
                Timber.d("Loading PDI data for dealer: $dealerCode")
                val response = pdiApi.getPdi(dealerCode)
                _pdiState.postValue(PdiState.Success(PdiData(response)))
                Timber.d("Successfully loaded ${response.size} PDI records")
            } catch (e: Exception) {
                Timber.e(e, "Error loading PDI data for dealer: $dealerCode")
                _pdiState.postValue(PdiState.Error("Error loading PDI data: ${e.message}"))
            }
        }
    }
}

sealed class PdiState {
    object Loading : PdiState()
    data class Success(val data: List<Map<String, String?>>) : PdiState()
    data class Error(val message: String) : PdiState()
}