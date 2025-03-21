package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.models.BydCarModel
import com.prototype.silver_tab.data.models.BydCarModels
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.session.AppSessionManager
import com.prototype.silver_tab.utils.getCarImageResource
import com.prototype.silver_tab.utils.logTimber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseCarViewModel @Inject constructor(
    private val appSessionManager: AppSessionManager
) : ViewModel() {
    private val tag = "ChooseCarViewModel"

    // All available car models
    private val allCarModels = BydCarModels.modelsList

    // Current search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered car models based on search
    private val _filteredCarModels = MutableStateFlow(allCarModels)
    val filteredCarModels: StateFlow<List<BydCarModel>> = _filteredCarModels.asStateFlow()

    init {
        logTimber(tag, "ChooseCarViewModel initialized with ${allCarModels.size} car models")
    }


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterCarModels()
    }


    private fun filterCarModels() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) {
            _filteredCarModels.value = allCarModels
            return
        }

        val filtered = allCarModels.filter {
            it.name.contains(query, ignoreCase = true)
        }
        _filteredCarModels.value = filtered
        logTimber(tag, "Filtered to ${filtered.size} cars with query: $query")
    }


    fun createInspectionInfo(model: BydCarModel): InspectionInfo {
        return InspectionInfo(
            carId = null,  // Will be assigned when car is created in backend
            vin = null,    // Will be entered on the CheckScreen
            name = model.name,
            type = model.type,
            // imageResId = getCarImageResource(model.name)
        )
    }

    // Add function to save selected car to session
    fun selectCarForInspection(car: InspectionInfo) {
        viewModelScope.launch {
            appSessionManager.selectInspection(car)
            logTimber(tag, "Saved car to session: ${car.name}")
        }
    }
}
