package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import com.prototype.silver_tab.data.models.InspectionInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedCarViewModel : ViewModel() {
    // Usando StateFlow para observar mudanças na lista
    private val _listHistoricCars = MutableStateFlow<List<InspectionInfo>>(emptyList())
    val listHistoricCars: StateFlow<List<InspectionInfo>> get() = _listHistoricCars

    fun updateListHistoricCars(newList: List<InspectionInfo>) {
        _listHistoricCars.value = newList
    }

}