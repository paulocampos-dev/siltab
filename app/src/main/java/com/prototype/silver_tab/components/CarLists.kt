package com.prototype.silver_tab.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.R
import com.prototype.silver_tab.ui.theme.BackgroundColor

data class Car(
    val name: String,
    val type: String? = null,
    val date: String? = null,
    val image: Int? = null, // ID do recurso drawable
    val route: String? =  null,
    val soc: Float? = null,
    val DE: Int? = null,
    val DD: Int? = null,
    val TD: Int? = null,
    val TE: Int? = null
)

val fakeCarList = listOf(
    Car("AAA BBBBB X CCCCCECC", "5 dias atrás"),//R.drawable.car1
    Car("AAA BBBBB X CCCCCCCC", "7 dias atrás", ),
    Car("AAA EEEEE X CCCCCCCC", "10 dias atrás", ),
    Car("AAA BBBBB X CCCCCCCC", "11 dias atrás", ),
    Car("AAA BBBBB X CCCCCCCC", "13 dias atrás", ),
    Car("AAA BBBBB X CCCCCCCC", "17 dias atrás", ),
    Car("AAA BBBBB X CCCCCCCC", "18 dias atrás", )
)
val BydCarsList = listOf(
    Car("Dolphin", type = "Eletric", route = "dolphin_route"),
    Car("Shark", type = "Eletric", route = "shark_route"),
    Car("Han", type = "Eletric", route = "han_route"),
    Car("Tan", type = "Eletric", route = "tan_route")
)

@Composable
fun CarCard(car: Car, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = car.image?.let { painterResource(it) } ?: painterResource(R.drawable.pid_car),//colocar alguma imagem aqui),
                contentDescription = car.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(car.name, color = Color.White, fontWeight = FontWeight.Bold)
                if (car.type != null) {
                    Text(text = car.type, color = Color.Gray)
                }
                if (car.date != null) {
                    Text(text = car.date, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun CarList(carList: List<Car>, onCarClicked: (Car) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items = carList) { car ->
            CarCard(car = car, onClick = { onCarClicked(car) })
        }
    }
}

@Composable
fun CarModalDialog(car: Car, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            surface = Color.White // Cor do fundo do diálogo
        )  //ver melhor como mudar a cor de alerts
    ){AlertDialog(
        modifier = Modifier.fillMaxHeight(),
        onDismissRequest = onDismiss,
        title = { Text(text = "Detalhes do Carro", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Nome: ${car.name}")
                Text("Última atualização: ${car.date}")
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = car.image?.let { painterResource(it) } ?: painterResource(R.drawable.pid_car),
                    contentDescription = car.name,
                    modifier = Modifier
                        .aspectRatio(16 / 9f)
                        )
                //histórico do chassi
                Text("Chassi:")
                Text(car.name, fontWeight = FontWeight.Bold)
                Image(
                    painter = painterResource(R.drawable.chassi_exemple),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("SOC %")
                Text(text = car.soc.toString())
                Image(
                    painter = painterResource(R.drawable.soc_example),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                //Ver como deixar os textos certinhos
                Text("Pressão dos Pneus")
                Text(text = car.DD?.toString() ?: "XX")
                Text(text = car.DE?.toString() ?: "XX")
                Text(text = car.TD?.toString() ?: "XX")
                Text(text = car.TE?.toString() ?: "XX")
                Image(
                    painter = painterResource(R.drawable.car_draw),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Fechar")
            }
        }

    )
    }
}