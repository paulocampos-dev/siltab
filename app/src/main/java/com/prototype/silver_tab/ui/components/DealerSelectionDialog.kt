package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.StringResources

//import com.prototype.silver_tab.data.models.DealerSummary

@Composable
fun DealerSelectionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onDealerSelected: (DealerSummary) -> Unit,
    dealerState: DealerState,
    strings: StringResources = LocalStringResources.current
) {
    if (showDialog) {
        var searchQuery by remember { mutableStateOf("") }

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.8f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Title
                    Text(
                        text = strings.selectDealer,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        placeholder = { Text(strings.searchDealers) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        },
                        singleLine = true
                    )

                    // Dealer List
                    when (dealerState) {
                        is DealerState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is DealerState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dealerState.message,
                                    color = Color.Red
                                )
                            }
                        }
                        is DealerState.Success -> {
                            val filteredDealers = dealerState.dealers.filter { dealer ->
                                dealer.dealerName.contains(searchQuery, ignoreCase = true) ||
                                        dealer.dealerCode.contains(searchQuery, ignoreCase = true)
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredDealers) { dealer ->
                                    DealerCard(
                                        dealer = dealer,
                                        strings = LocalStringResources.current,
                                        onDealerClick = {
                                            onDealerSelected(dealer)
                                            onDismiss()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DealerCard(
    dealer: DealerSummary,
    strings: StringResources,
    onDealerClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDealerClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = dealer.dealerName,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = "${strings.dealerCode}: ",
                    color = Color.Black,
                )
                Text(text = dealer.dealerCode, color = Color(0xFFA9A9A9))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = "${strings.region}: ",
                    color = Color.Black,
                )
                Text(text = dealer.region ?: "N/A", color = Color(0xFFA9A9A9))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = "${strings.status}: ",
                    color = Color.DarkGray
                )
                Text(text = dealer.operationStatusName ?: "N/A", color = Color(0xFFA9A9A9))
            }
        }
    }
}

sealed class DealerState {
    object Loading : DealerState()
    data class Success(val dealers: List<DealerSummary>) : DealerState()
    data class Error(val message: String) : DealerState()
}

data class DealerSummary(
    val dealerCode: String,
    val dealerName: String,
    val groupName: String? = null,
    val region: String? = null,
    val regionalManagerName: String? = null,
    val contactNumber: String? = null,
    val email: String? = null,
    val operationStatusName: String? = null,
    val operationServiceScopeName: String? = null,
    val operationAfterSale: String? = null
)