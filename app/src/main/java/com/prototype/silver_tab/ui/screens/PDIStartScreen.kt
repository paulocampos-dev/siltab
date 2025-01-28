package com.prototype.silver_tab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.prototype.silver_tab.R
import com.prototype.silver_tab.ui.components.Car
import com.prototype.silver_tab.ui.components.CarList
import com.prototype.silver_tab.ui.components.CarModalDialog
import com.prototype.silver_tab.ui.components.SearchBar
import com.prototype.silver_tab.ui.components.fakeCarList
import com.prototype.silver_tab.ui.theme.BackgroundColor

@Composable
fun PDIStartScreen(
    modifier: Modifier = Modifier,
    onPDIStartButtonClicked: () -> Unit,
){
    var selectedCar: Car? by remember { mutableStateOf(null) }
    var searchCar by remember { mutableStateOf("") }
    val filteredCarList = fakeCarList.filter {
        it.name.contains(searchCar, ignoreCase = true)
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .background(BackgroundColor)
        ) {
        //card da concessionária
        Card(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundColor)
        ) {
            Row(
                modifier = Modifier.padding(16.dp)
                    .widthIn(max = 350.dp) //ajeitar isso para ficar alinhado
                    .border(1.dp, Color.White, shape = RoundedCornerShape(20.dp))
                    .padding(8.dp)
                    .background(BackgroundColor, shape = RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = "Localização",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        "Nome da Concessionária",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Endereço da Concessionária", color = Color.Gray)
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = "Configuração",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Botão para iniciar o pdi
        Button(
            onClick = onPDIStartButtonClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Unspecified
            ),
            shape = RectangleShape,
            modifier = Modifier.wrapContentWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                ConstraintLayout(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val (button, car) = createRefs()

                    Image(
                        painter = painterResource(R.drawable.pidstart_button),
                        contentDescription = "PDI Button",
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(button) {
                                width = Dimension.fillToConstraints
                                height = Dimension.wrapContent
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )


                    Image(
                        painter = painterResource(R.drawable.pid_car),
                        contentDescription = "Car icon for stock button",
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .aspectRatio(2f)
                            .constrainAs(car) {
                                width = Dimension.fillToConstraints
                                height = Dimension.wrapContent
                                end.linkTo(button.end, margin = (0).dp)
                                bottom.linkTo(button.bottom, margin = 0.dp)
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height((dimensionResource(R.dimen.padding_small))))
        Column (modifier = Modifier.fillMaxWidth()
            .background(color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        ){

            //Criar barra de pesquisa
            SearchBar(query = searchCar,
                onQueryChange = {searchCar = it})




        // Lista de carros
        CarList(carList = filteredCarList) { car ->
            selectedCar = car
        }

        // Modal de detalhes do carro
            selectedCar?.let { car ->
                CarModalDialog(
                    car = car, onDismiss = { selectedCar = null },

                    )
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewPDIStartScreen() {
    PDIStartScreen(
        onPDIStartButtonClicked = { },
        modifier = Modifier.fillMaxSize()
    )
}








