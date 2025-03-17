package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.models.BydCarModel
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.language.LocalStringResources
import com.prototype.silver_tab.ui.components.SearchBar
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.utils.getCarImageResource
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.viewmodels.ChooseCarViewModel
import timber.log.Timber

@Composable
fun ChooseCarScreen(
    onCarSelected: (InspectionInfo) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChooseCarViewModel = hiltViewModel()
) {
    logTimber("ChooseCarScreen", "ChooseCarScreen initialized")

    val strings = LocalStringResources.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredCarModels by viewModel.filteredCarModels.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .then(modifier)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = strings.chooseCarModel,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main content card with search and car list
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            colors = CardDefaults.cardColors(
//                containerColor = Color(0xFFD9D9D9)
                containerColor = Color(0xFF535353)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Search bar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    placeholder = strings.searchCars,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Car list
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredCarModels) { carModel ->
                        CarModelItem(
                            carModel = carModel,
                            onCarSelected = { model ->
                                val inspectionInfo = viewModel.createInspectionInfo(model)
                                onCarSelected(inspectionInfo)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CarModelItem(
    carModel: BydCarModel,
    onCarSelected: (BydCarModel) -> Unit
) {
    val context = LocalContext.current
    val imageResId = getCarImageResource(carModel.name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onCarSelected(carModel) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
//            containerColor = Color.White
            containerColor = Color(0xFFbcbcbc)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Car image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = carModel.name,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Car details
            Column {
                Text(
                    text = carModel.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = carModel.type,
                    style = MaterialTheme.typography.bodyMedium,
//                    color = if (carModel.type == "Hybrid") Color(0xFF006400) else Color(0xFF1976D2)
                    color = Color.Black
                )
            }
        }
    }
}