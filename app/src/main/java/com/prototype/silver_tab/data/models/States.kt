package com.prototype.silver_tab.data.models


sealed class DealerState {
    data class Success(val dealers: List<DealerSummary>) : DealerState()
    data class Error(val message: String) : DealerState()
    object Loading : DealerState()
}
