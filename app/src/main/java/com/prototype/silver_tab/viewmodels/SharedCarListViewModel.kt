package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import com.prototype.silver_tab.data.models.CarResponse
import com.prototype.silver_tab.data.models.InspectionInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class SharedCarViewModel : ViewModel() {
    // Existing list for cars with PDI history
    private val _listHistoricCars = MutableStateFlow<List<InspectionInfo>>(emptyList())
    val listHistoricCars: StateFlow<List<InspectionInfo>> get() = _listHistoricCars

    // New list for all dealer cars
    private val _listAllDealerCars = MutableStateFlow<List<CarResponse>>(emptyList())
    val listAllDealerCars: StateFlow<List<CarResponse>> get() = _listAllDealerCars

    fun updateListHistoricCars(newList: List<InspectionInfo>) {
        _listHistoricCars.value = newList
    }

    fun updateListAllDealerCars(newList: List<CarResponse>) {
        _listAllDealerCars.value = newList
    }
}
