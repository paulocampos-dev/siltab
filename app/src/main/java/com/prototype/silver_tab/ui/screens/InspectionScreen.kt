package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prototype.silver_tab.ui.components.SearchBar
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.ui.components.DealerSelectionCard
import com.prototype.silver_tab.ui.components.DealerSelectionDialog
import com.prototype.silver_tab.ui.components.InspectionInfoCard
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.language.LocalStringResources
import com.prototype.silver_tab.language.LocalizedDrawables
import com.prototype.silver_tab.language.LocalizedImage
import com.prototype.silver_tab.viewmodels.InspectionScreenViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InspectionScreen(
    onStartNewInspection: () -> Unit,
    onViewInspectionDetails: (InspectionInfo) -> Unit,
    onUpdateInspection: (InspectionInfo) -> Unit,
    viewModel: InspectionScreenViewModel = hiltViewModel()
) {
    val dealerState by viewModel.dealerState.collectAsState()
    val selectedDealer by viewModel.selectedDealer.collectAsState()
    val inspections by viewModel.filteredInspections.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showDealerDialog by remember { mutableStateOf(false) }
    var selectedInspectionInfo by remember { mutableStateOf<InspectionInfo?>(null) }
    var showNoDealerDialog by remember { mutableStateOf(false) }

    val refreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.refreshAllData() }
    )

    val strings = LocalStringResources.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .pullRefresh(refreshState)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Show error message if there is one
            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Dealer selection card
                item {
                    DealerSelectionCard(
                        selectedDealer = selectedDealer,
                        onClick = { showDealerDialog = true },
                        strings = strings
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Start inspection button
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                if (selectedDealer == null) {
                                    showNoDealerDialog = true
                                } else {
                                    onStartNewInspection()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            LocalizedImage(
                                drawableMap = LocalizedDrawables.pdiButton,
//                                contentDescription = strings.startInspection
                                contentDescription = "lolized"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Search bar
                item {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        placeholder = strings.searchCars
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Inspection list or loading indicator
                if (isLoading && inspections.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                } else if (inspections.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No inspections found for this dealer",
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // Inspection list
                    items(inspections) { inspection ->
                        InspectionInfoCard(
                            inspectionInfo = inspection,
                            onClick = { selectedInspectionInfo = inspection }
                        )
                    }
                }
            }
        }

        // Pull refresh indicator
        PullRefreshIndicator(
            refreshing = isLoading,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Dialogs
        if (showDealerDialog) {
            DealerSelectionDialog(
                showDialog = true,
                onDismiss = { showDealerDialog = false },
                onDealerSelected = { dealer -> viewModel.selectDealer(dealer) },
                dealerState = dealerState
            )
        }

        selectedInspectionInfo?.let { inspection ->
//            InspectionInfoModalDialog(
//                inspectionInfo = inspection,
//                onDismiss = { selectedInspectionInfo = null },
//                onNewPdi = { onStartNewInspection() },
//                onChangeHistoricPDI = { onUpdateInspection(it) }
//            )
        }

        if (showNoDealerDialog) {
            AlertDialog(
                onDismissRequest = { showNoDealerDialog = false },
                title = { Text(strings.selectDealerRequired) },
                text = { Text(strings.selectDealerRequiredDesc) },
                confirmButton = {
                    Button(onClick = {
                        showNoDealerDialog = false
                        showDealerDialog = true
                    }) {
                        Text(strings.selectDealer)
                    }
                },
                dismissButton = {
                    Button(onClick = { showNoDealerDialog = false }) {
                        Text(strings.close)
                    }
                }
            )
        }
    }
}