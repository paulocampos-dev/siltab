package com.prototype.silver_tab.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CarsDataViewModelFactory(
    private val dealerViewModel: DealerViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarsDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarsDataViewModel(dealerViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}