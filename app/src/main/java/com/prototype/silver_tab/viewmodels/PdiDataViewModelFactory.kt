package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PdiDataViewModelFactory(
    private val dealerViewModel: DealerViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PdiDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PdiDataViewModel(dealerViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}