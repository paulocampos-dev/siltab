package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prototype.silver_tab.data.models.BydInspectionInfos
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.ui.components.InspectionInfoList
import com.prototype.silver_tab.ui.components.SearchBar
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.viewmodels.SharedCarViewModel
import timber.log.Timber

@Composable
fun ChooseCar(
    onCarSelected: (InspectionInfo) -> Unit,
    modifier: Modifier = Modifier,
    // Use Hilt for ViewModel injection
    sharedCarViewModel: SharedCarViewModel = hiltViewModel()
) {
    Timber.d("ChooseCar screen initialized with Hilt")

    var searchCar by remember { mutableStateOf("") }
    val strings = LocalStringResources.current
    val filteredCarList = BydInspectionInfos.filter {
        it.name?.contains(searchCar, ignoreCase = true) ?: false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = strings.pdiTitle,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
        Spacer(modifier = Modifier.height(36.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .background(color = Color(0xFFD9D9D9),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
        ) {


            SearchBar(
                query = searchCar,
                onQueryChange = { searchCar = it },
                placeholder = strings.searchCars,
            )
            InspectionInfoList(
                inspectionInfoList = filteredCarList,
                onCarClicked = { selectedCar ->
                    onCarSelected(selectedCar)
                }
            )
        }
    }
}