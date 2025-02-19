package com.prototype.silver_tab.viewmodels

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

class PdiDataViewModel : ViewModel() {
    private val _pdiState = MutableLiveData<PdiState>(PdiState.Loading)
    val pdiState: LiveData<PdiState> = _pdiState


    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.pdiApi.getPdi()
                _pdiState.value = PdiState.Success(PdiData(response))

            } catch (e: Exception) {
                _pdiState.value = PdiState.Error("Erro ao carregar: ${e.message}")
            }
        }
    }
}

sealed class PdiState {
    object Loading : PdiState()
    data class Success(val data: List<Map<String, String?>>) : PdiState()
    data class Error(val message: String) : PdiState()
}

