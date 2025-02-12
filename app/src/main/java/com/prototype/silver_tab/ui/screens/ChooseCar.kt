package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.data.models.BydInspectionInfos
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.ui.components.InspectionInfoList
import com.prototype.silver_tab.ui.components.SearchBar
import com.prototype.silver_tab.ui.theme.BackgroundColor

@Composable
fun ChooseCar(
    onCarSelected: (InspectionInfo) -> Unit,    // ver como fazer esse por que não vai ser um alert e sim um navigate de uma lista de carro
    modifier: Modifier = Modifier,
) {
    var searchCar by remember { mutableStateOf("") }
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Qual o modelo do carro que você fará o PDI?",
                color = Color.White,
                fontWeight = FontWeight.Bold
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


            SearchBar(query = searchCar,
                onQueryChange = { searchCar = it })
            InspectionInfoList(
                inspectionInfoList = filteredCarList,
                onCarClicked = { selectedCar ->
                onCarSelected(selectedCar)
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChooseCar() {
    ChooseCar(
        onCarSelected = { selectedCar ->
            // Simulate navigation or action when a car is selected
        },
        modifier = Modifier.fillMaxSize()
    )
}
