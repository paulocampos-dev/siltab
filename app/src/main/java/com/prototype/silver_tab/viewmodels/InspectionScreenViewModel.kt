package com.prototype.silver_tab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prototype.silver_tab.data.models.DealerState
import com.prototype.silver_tab.data.models.DealerSummary
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.car.Car
import com.prototype.silver_tab.data.models.car.CarState
import com.prototype.silver_tab.data.models.pdi.InspectionState
import com.prototype.silver_tab.data.repository.AuthRepository
import com.prototype.silver_tab.data.repository.CarRepository
import com.prototype.silver_tab.data.repository.DealerRepository
import com.prototype.silver_tab.data.repository.InspectionRepository
import com.prototype.silver_tab.session.AppSessionManager
import com.prototype.silver_tab.utils.convertPdiToInspectionInfo
import com.prototype.silver_tab.utils.logTimber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

// Define sort orders
enum class SortOrder {
    NEWEST_FIRST,
    OLDEST_FIRST
}

@HiltViewModel
class InspectionScreenViewModel @Inject constructor(
    private val dealerRepository: DealerRepository,
    private val carRepository: CarRepository,
    private val inspectionRepository: InspectionRepository,
    private val authRepository: AuthRepository,
    private val appSessionManager: AppSessionManager
) : ViewModel() {

    val tag = "InspectionScreenViewModel"

    // Dealer state and selection
    val dealerState = dealerRepository.dealerState
    val selectedDealer = dealerRepository.selectedDealer
    val possibleDealers = dealerRepository.possibleDealers

    // Cars data
    val carState = carRepository.carState

    // Inspection data
    val inspectionState = inspectionRepository.inspectionState

    // Processed inspection info for the UI
    private val _inspectionInfoList = MutableStateFlow<List<InspectionInfo>>(emptyList())
    val inspectionInfoList: StateFlow<List<InspectionInfo>> = _inspectionInfoList.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Search query for filtering
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Data loading job
    private var dataLoadingJob: Job? = null

    // Sort order state
    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST_FIRST)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    // Show only pending
    private val _showOnlyPending = MutableStateFlow(false)
    val showOnlyPending: StateFlow<Boolean> = _showOnlyPending.asStateFlow()

    val canChangeDealer = authRepository.authState.map { authState ->
        // Position 2 or higher can change dealers
        val position = authState.position ?: 0
        position >= 2
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Filtered inspections
    val filteredInspections = combine(
        inspectionInfoList,
        searchQuery,
        sortOrder,
        showOnlyPending
    ) { inspections, query, order, onlyPending ->
        logTimber(tag, "Filtering inspections with query: $query and sort order: $order, only pending: $onlyPending")

        // Filtering out sold cars
        var filtered = inspections.filter { !it.isSold }

        // Filtering pending
        if (onlyPending) {
            filtered = filtered.filter { it.pending == true }
        }

        // Filtering by query
        filtered = if (query.isBlank()) filtered
        else filtered.filter {
            it.vin?.contains(query, ignoreCase = true) == true ||
                    it.name?.contains(query, ignoreCase = true) == true
        }

        when (order) {
            SortOrder.NEWEST_FIRST -> filtered.sortedByDescending { it.date }
            SortOrder.OLDEST_FIRST -> filtered.sortedBy { it.date }
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    init {
        logTimber(tag, "Initializing InspectionScreenViewModel")


        logTimber(tag, "Refreshing Dealers")
        refreshDealers()

        logTimber(tag, "Observing dealer state")
        // Check for any previously selected dealer in the session
        viewModelScope.launch {
            appSessionManager.selectedDealer.collect { sessionDealer ->
                if (sessionDealer != null && dealerRepository.selectedDealer.value == null) {
                    logTimber(tag, "Restoring dealer from session: ${sessionDealer.dealerCode}")
                    dealerRepository.selectDealer(sessionDealer)
                }
            }
        }

        // Observe dealer changes to load data
        viewModelScope.launch {
            selectedDealer.collect { dealer ->
                logTimber(tag, "Selected dealer changed: ${dealer?.dealerCode}")

                // Clear previous data immediately to avoid showing stale data
                _inspectionInfoList.value = emptyList()
                _isLoading.value = true

                dealer?.let {
                    delay(50)  // Short delay to let UI update
                    loadDealerData(it.dealerCode, forceRefresh = true)  // Force refresh on dealer change
                }
            }
        }

        // Monitor car and inspection state to combine data
        viewModelScope.launch {
            combine(carState, inspectionState) { carsState, inspState ->
                _isLoading.value = carsState is CarState.Loading || inspState is InspectionState.Loading

                if (carsState is CarState.Error) {
                    _error.value = "Error loading cars: ${carsState.message}"
                } else if (inspState is InspectionState.Error) {
                    _error.value = "Error loading inspections: ${inspState.message}"
                } else {
                    _error.value = null
                }

                processData()
            }.catch { throwable ->
                logTimber(tag, "Error in data stream: ${throwable.message}")
                _error.value = "Error processing data: ${throwable.message}"
            }.collect { /* Just collecting to trigger the processing */ }
        }
    }

    fun selectDealer(dealer: DealerSummary) {
        logTimber(tag, "Selecting dealer: ${dealer.dealerCode}")
        dealerRepository.selectDealer(dealer)
        appSessionManager.selectDealer(dealer)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSortOrder() {
        _sortOrder.value = when (_sortOrder.value) {
            SortOrder.NEWEST_FIRST -> SortOrder.OLDEST_FIRST
            SortOrder.OLDEST_FIRST -> SortOrder.NEWEST_FIRST
        }
        logTimber(tag, "Sort order changed to: ${_sortOrder.value}")
    }

    fun refreshDealers() {
        viewModelScope.launch {
            logTimber(tag, "Refreshing dealers")
            _isLoading.value = true
            try {
                dealerRepository.loadDealers()

                // If there's only one dealer, auto-select it and ensure it's saved to session
                val currentState = dealerState.value
                if (currentState is DealerState.Success) {
                    if (currentState.dealers.size == 1) {
                        val onlyDealer = currentState.dealers.first()

                        logTimber(tag, "Auto-selecting single dealer: ${onlyDealer.dealerCode}")

                        // Select in repository
                        dealerRepository.selectDealer(onlyDealer)

                        // Save to session explicitly
                        appSessionManager.selectDealer(onlyDealer)

                        logTimber(tag, "Dealer saved to session: ${onlyDealer.dealerCode}")
                    } else {
                        logTimber(tag, "Found ${currentState.dealers.size} dealers, not auto-selecting")
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                logTimber(tag, "Error refreshing dealers: ${e.message}")
                _error.value = "Failed to load dealers: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun ensureDealerSelected(): Boolean {
        // Check if we already have a selected dealer
        if (selectedDealer.value != null) {
            logTimber(tag, "Dealer already selected: ${selectedDealer.value?.dealerCode}")
            return true
        }

        // If not, check if we can auto-select one
        val currentState = dealerState.value
        if (currentState is DealerState.Success && currentState.dealers.size == 1) {
            val onlyDealer = currentState.dealers.first()
            logTimber(tag, "Auto-selecting only dealer: ${onlyDealer.dealerCode}")

            // Select in repository
            dealerRepository.selectDealer(onlyDealer)

            // Save to session explicitly
            appSessionManager.selectDealer(onlyDealer)

            logTimber(tag, "Dealer selection restored: ${onlyDealer.dealerCode}")
            return true
        }

        // No dealer selected and can't auto-select
        logTimber(tag, "No dealer selected and multiple options available")
        return false
    }

    fun refreshAllData() {
        viewModelScope.launch {
            logTimber(tag, "Starting refreshAllData")

            // Clear existing data first
            _inspectionInfoList.value = emptyList()

            // Set loading state
            _isLoading.value = true
            _error.value = null

            val dealer = selectedDealer.value
            if (dealer != null) {
                logTimber(tag, "Refreshing data for dealer: ${dealer.dealerCode}")

                // Clear any cached data for this dealer in repositories
                carRepository.clearCache(dealer.dealerCode)

                // Force reload all data
                loadDealerData(dealer.dealerCode, forceRefresh = true)
            } else {
                // Don't show "No dealer selected" error on initial load
                _isLoading.value = false

                // Only show empty data, but not as an error
                _inspectionInfoList.value = emptyList()

                logTimber(tag, "No dealer selected, skipping refresh")
            }
        }
    }

    private fun loadDealerData(dealerCode: String, forceRefresh: Boolean = false) {
        // Cancel any ongoing data loading
        dataLoadingJob?.cancel()

        dataLoadingJob = viewModelScope.launch {
            logTimber(tag, "Started loading dealer data for dealer $dealerCode")
            _isLoading.value = true
            _error.value = null

            try {
                // Load cars and inspections in parallel
                val carsJob = launch {
                    try {
                        logTimber(tag, "Loading cars for dealer: $dealerCode")
                        carRepository.getDealerCars(dealerCode, forceRefresh)
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        logTimber(tag, "Error loading cars: ${e.message}")
                        _error.value = "Error loading cars: ${e.message}"
                    }
                }

                val inspectionsJob = launch {
                    try {
                        logTimber(tag, "Loading inspections for dealer: $dealerCode")
                        inspectionRepository.getDealerInspections(dealerCode, forceRefresh)
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        logTimber(tag, "Error loading inspections: ${e.message}")
                        _error.value = "Error loading inspections: ${e.message}"
                    }
                }

                // Wait for both to complete
                carsJob.join()
                inspectionsJob.join()

                // Process the data
                processData()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Timber.e(e, "Error loading dealer data")
                _error.value = "Error loading data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Process and combine car and inspection data
    private fun processData() {
        logTimber(tag, "Started processing data")
        viewModelScope.launch {
            try {
                val carsData = when (val state = carState.value) {
                    is CarState.Success -> state.cars
                    else -> emptyList()
                }

                val inspectionsData = when (val state = inspectionState.value) {
                    is InspectionState.Success -> state.inspections
                    else -> emptyList()
                }

                // Log what we have to work with
                logTimber(tag, "Processing ${carsData.size} cars and ${inspectionsData.size} inspections")

                // Continue even if one of the lists is empty
                // Create a map of car ID to the most recent PDI for that car
                val latestPdisByCar = inspectionsData
                    .groupBy { it.carId }
                    .mapValues { (_, pdis) ->
                        pdis.maxByOrNull { pdi ->
                            pdi.createdDate ?: ""
                        }
                    }

                logTimber(tag, "Found ${latestPdisByCar.size} unique cars with PDIs")

                // Create inspection info for each car
                val inspectionInfos = mutableListOf<InspectionInfo>()

                // First, process cars that have PDIs
                for (car in carsData) {
                    val pdi = latestPdisByCar[car.carId]
                    if (pdi != null) {
                        val info = convertPdiToInspectionInfo(pdi, car)
                        inspectionInfos.add(info)
                        logTimber(tag, "Added inspection for car ${car.vin} with PDI ${pdi.pdiId}")
                    }
                }

                // Log results
                logTimber(tag, "Generated ${inspectionInfos.size} inspection info entries")

                // Update the state with what we have
                _inspectionInfoList.value = inspectionInfos
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                logTimber(tag, "Error processing data: ${e.message}")
                _error.value = "Error processing data: ${e.message}"
            }
        }
    }

    fun togglePendingFilter() {
        logTimber(tag, "Pending filter toggled to: ${_showOnlyPending.value}")
        _showOnlyPending.value = !_showOnlyPending.value
    }

    fun startNewInspection(carInfo: InspectionInfo) {
        // First ensure a dealer is selected
        if (!ensureDealerSelected()) {
            _error.value = "Please select a dealer first"
            return
        }

        // Set the dealer code on the inspection info
        val inspectionWithDealer = selectedDealer.value?.let { dealer ->
            carInfo.copy(dealerCode = dealer.dealerCode)
        } ?: carInfo

        // Save to session
        appSessionManager.selectInspection(inspectionWithDealer)

        // Navigation will be handled by the UI
    }

    override fun onCleared() {
        super.onCleared()
        dataLoadingJob?.cancel()
    }
}