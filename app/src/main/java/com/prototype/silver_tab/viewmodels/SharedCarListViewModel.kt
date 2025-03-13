package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import com.prototype.silver_tab.data.models.CarResponse
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.repository.SharedCarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject

/**
 * Shared ViewModel for car data across different screens.
 * Updated to use Hilt for dependency injection and SharedCarRepository.
 */
@HiltViewModel
class SharedCarViewModel @Inject constructor(
    private val sharedCarRepository: SharedCarRepository
) : ViewModel() {
    // Expose the StateFlows from the repository
    val listHistoricCars: StateFlow<List<InspectionInfo>> = sharedCarRepository.listHistoricCars
    val listAllDealerCars: StateFlow<List<CarResponse>> = sharedCarRepository.listAllDealerCars

    init {
        Timber.d("SharedCarViewModel initialized with Hilt")
    }

    fun updateListHistoricCars(newList: List<InspectionInfo>) {
        Timber.d("Updating historic cars list with ${newList.size} items")
        sharedCarRepository.updateListHistoricCars(newList)
    }

    fun updateListAllDealerCars(newList: List<CarResponse>) {
        Timber.d("Updating all dealer cars list with ${newList.size} items")
        sharedCarRepository.updateListAllDealerCars(newList)
    }
}