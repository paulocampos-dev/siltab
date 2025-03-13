package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.data.repository.DealerRepository
import com.prototype.silver_tab.data.repository.DealerSelectionRepository
import com.prototype.silver_tab.ui.components.DealerState
import com.prototype.silver_tab.ui.components.DealerSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DealerViewModel @Inject constructor(
    private val dealerRepository: DealerRepository,
    private val dealerSelectionRepository: DealerSelectionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    // Expose the repository StateFlows
    val dealerState: StateFlow<DealerState> = dealerRepository.dealerState

    // Use the shared repository's selected dealer
    val selectedDealer: StateFlow<DealerSummary?> = dealerSelectionRepository.selectedDealer

    init {
        Timber.d("DealerViewModel initialized")
        viewModelScope.launch {
            // Observe auth state changes
            authRepository.authState.collectLatest { state ->
                if (state.isAuthenticated) {
                    Timber.d("User authenticated, loading dealers")
                    dealerRepository.loadDealers()
                } else {
                    // Clear dealer state when logged out
                    Timber.d("User not authenticated, clearing dealer state")
                    dealerRepository.clearDealerState()
                    dealerSelectionRepository.clearSelectedDealer()
                }
            }
        }
    }

    fun notifyAuthenticated() {
        // This method is now just a trigger to force reload dealers
        Timber.d("Authentication notification received, reloading dealers")
        viewModelScope.launch {
            dealerRepository.loadDealers()
        }
    }

    fun selectDealer(dealer: DealerSummary) {
        Timber.d("Dealer selection requested: ${dealer.dealerCode}")
        dealerRepository.selectDealer(dealer)

        // Also update the shared repository
        dealerSelectionRepository.selectDealer(dealer)
    }

    fun refreshDealers() {
        Timber.d("Manual dealer refresh requested")
        viewModelScope.launch {
            dealerRepository.loadDealers()
        }
    }
}