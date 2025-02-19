package com.prototype.silver_tab.viewmodels

import com.prototype.silver_tab.data.mappers.CarsData
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prototype.silver_tab.data.api.RetrofitClient
import com.prototype.silver_tab.data.mappers.PdiData
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class CarsDataViewModel : ViewModel() {
    private val _carsState = MutableLiveData<CarsState>(CarsState.Loading)
    val carsState: LiveData<CarsState> = _carsState


    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.carsApi.getCars()
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
