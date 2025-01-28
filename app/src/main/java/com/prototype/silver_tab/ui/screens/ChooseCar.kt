package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.components.BydCarsList
import com.prototype.silver_tab.components.Car
import com.prototype.silver_tab.components.CarList
import com.prototype.silver_tab.components.SearchBar
import com.prototype.silver_tab.components.fakeCarList
import com.prototype.silver_tab.ui.theme.BackgroundColor

@Composable
fun ChooseCar(
    onCarSelected: (Car) -> Unit,    // ver como fazer esse por que não vai ser um alert e sim um navigate de uma lista de carro
    modifier: Modifier = Modifier,
) {
    var searchCar by remember { mutableStateOf("") }
    val filteredCarList = BydCarsList.filter {
        it.name.contains(searchCar, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        Text(text = "Qual o modelo do carro que você fará o PDI?", color = Color.White)

        Spacer(modifier = Modifier.height(36.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
        ) {


            SearchBar(query = searchCar,
                onQueryChange = { searchCar = it })
            CarList(carList = filteredCarList, onCarClicked = { selectedCar ->
                onCarSelected(selectedCar)
            })
        }
    }
}