package com.prototype.silver_tab.data.models.car

sealed class CarState {
    data class Success(val cars: List<Car>) : CarState()
    data class Error(val message: String) : CarState()
    data object Loading : CarState()
}
