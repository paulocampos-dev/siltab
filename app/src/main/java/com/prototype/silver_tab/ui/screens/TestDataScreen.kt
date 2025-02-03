package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prototype.silver_tab.viewmodels.CarsDataViewModel
import com.prototype.silver_tab.viewmodels.CarsState
import com.prototype.silver_tab.viewmodels.PdiState

@Composable
fun PdiDataMock() {
    val viewModel: CarsDataViewModel = viewModel()
    val state = viewModel.carsState.observeAsState().value ?: CarsState.Loading

    Scaffold { innerPadding ->
        when (state) {
            is CarsState.Loading -> LoadingUI(innerPadding)
            is CarsState.Success -> SuccessUI(state.data, innerPadding)
            is CarsState.Error -> ErrorUI(state.message, innerPadding)
        }
    }
}

@Composable
private fun LoadingUI(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Carregando dados...")
    }
}

@Composable
private fun SuccessUI(data: List<Map<String, String?>>, innerPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        items(data) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    item.forEach { (key, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = key,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = value.toString(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorUI(message: String, innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Erro: $message", color = MaterialTheme.colorScheme.error)
    }
}