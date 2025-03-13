package com.prototype.silver_tab.data.repository

import com.prototype.silver_tab.data.models.CarResponse
import com.prototype.silver_tab.data.models.InspectionInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for sharing car data across different ViewModels.
 * Using Singleton scope to ensure a single instance is shared across the app.
 */
@Singleton
class SharedCarRepository @Inject constructor() {
    // StateFlow for cars with PDI history
    private val _listHistoricCars = MutableStateFlow<List<InspectionInfo>>(emptyList())
    val listHistoricCars: StateFlow<List<InspectionInfo>> = _listHistoricCars.asStateFlow()

    // StateFlow for all dealer cars
    private val _listAllDealerCars = MutableStateFlow<List<CarResponse>>(emptyList())
    val listAllDealerCars: StateFlow<List<CarResponse>> = _listAllDealerCars.asStateFlow()

    /**
     * Update the list of historic cars
     */
    fun updateListHistoricCars(newList: List<InspectionInfo>) {
        Timber.d("Updating historic cars list with ${newList.size} items")
        _listHistoricCars.value = newList
    }

    /**
     * Update the list of all dealer cars
     */
    fun updateListAllDealerCars(newList: List<CarResponse>) {
        Timber.d("Updating all dealer cars list with ${newList.size} items")
        _listAllDealerCars.value = newList
    }
}