package com.prototype.silver_tab.data.repository

import com.prototype.silver_tab.language.StringResources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringResourceRepository @Inject constructor() {
    private val _currentStrings = MutableStateFlow(StringResources())
    val strings: StateFlow<StringResources> = _currentStrings.asStateFlow()

    fun updateStrings(newStrings: StringResources) {
        _currentStrings.value = newStrings
    }
}