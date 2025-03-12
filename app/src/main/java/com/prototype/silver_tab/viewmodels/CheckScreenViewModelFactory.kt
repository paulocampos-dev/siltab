package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prototype.silver_tab.data.repository.CheckScreenRepository

/**
 * Factory for creating a RefactoredCheckScreenViewModel with its dependencies.
 * This allows us to pass dependencies to the ViewModel when it's created by the
 * Android architecture components.
 */
class CheckScreenViewModelFactory(
    private val repository: CheckScreenRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckScreenViewModel::class.java)) {
            return CheckScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}