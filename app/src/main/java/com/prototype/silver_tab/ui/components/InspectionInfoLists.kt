package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.fakeInspectionInfoLists
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.utils.formatRelativeDate



@Composable
fun InspectionInfoCard(inspectionInfo: InspectionInfo, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
            var img = R.drawable.pid_car
            when (inspectionInfo.name) {
                "BYD YUAN PLUS" -> img = R.drawable.byd_yuan_plus
                "BYD TAN" -> img =  R.drawable.byd_tan
                "BYD YUAN PRO" -> img = R.drawable.byd_yuan_pro
                "BYD SEAL" -> img = R.drawable.pid_car
                "BYD HAN" -> img = R.drawable.byd_han
                "BYD DOLPHIN PLUS" -> img = R.drawable.byd_dolphin_plus
                "BYD DOLPHIN" -> img = R.drawable.byd_dolphin
                "BYD DOLPHIN MINI" -> img = R.drawable.byd_dolphin_mini
                "BYD SONG PRO DM-i" -> img = R.drawable.byd_song_pro
                "SONG PLUS PREMIUM DM-i" -> img = R.drawable.byd_song_plus
                "BYD SONG PLUS DM-i" -> img = R.drawable.byd_song_plus
                "BYD KING DM-i" -> img = R.drawable.byd_king
                "BYD SHARK" -> img = R.drawable.byd_shark
                else -> {
                }
            }
            Image(
                painter = inspectionInfo.image?.let { painterResource(it) } ?: painterResource(img),
                contentDescription = inspectionInfo.chassi,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                inspectionInfo.chassi?.let {
                    Text(it, color = Color.White, fontWeight = FontWeight.Bold)
                } ?: inspectionInfo.name?.let {Text(it, color = Color.White, fontWeight = FontWeight.Bold)}
                if (inspectionInfo.type != null) {
                    Text(text = inspectionInfo.type, color = Color.Gray)
                }
                if (inspectionInfo.date != null) {
                    Text(text = formatRelativeDate(inspectionInfo.date), color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun InspectionInfoList(inspectionInfoList: List<InspectionInfo>, onCarClicked: (InspectionInfo) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items = inspectionInfoList) { car ->
            InspectionInfoCard(inspectionInfo = car, onClick = { onCarClicked(car) })
        }
    }
}


@Composable
fun InpectionInfoModalDialog(inspectionInfo: InspectionInfo,
                             onDismiss: () -> Unit,
                             modifier: Modifier = Modifier,
                             onChangeHistoricPDI: (InspectionInfo) -> Unit,
                             onNewPdi: (InspectionInfo) -> Unit

) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            surface = Color.White // Cor do fundo do diálogo
        )  //ver melhor como mudar a cor de alerts
    ){AlertDialog(
        modifier = Modifier.fillMaxHeight(),
        onDismissRequest = onDismiss,
        title = { Text(text = "Detalhes do Carro", fontWeight = FontWeight.Bold) },
        text = {
            Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Column {
                    Text("Nome: ${inspectionInfo.name}")
                    Text("Última atualização: ${inspectionInfo.date}")
                    Spacer(modifier = Modifier.height(16.dp))
                    var img = R.drawable.pid_car
                    when (inspectionInfo.name) {
                        "BYD YUAN PLUS" -> img = R.drawable.byd_yuan_plus
                        "BYD TAN" -> img =  R.drawable.byd_tan
                        "BYD YUAN PRO" -> img = R.drawable.byd_yuan_pro
                        "BYD SEAL" -> img = R.drawable.pid_car
                        "BYD HAN" -> img = R.drawable.byd_han
                        "BYD DOLPHIN PLUS" -> img = R.drawable.byd_dolphin_plus
                        "BYD DOLPHIN" -> img = R.drawable.byd_dolphin
                        "BYD DOLPHIN MINI" -> img = R.drawable.byd_dolphin_mini
                        "BYD SONG PRO DM-i" -> img = R.drawable.byd_song_pro
                        "SONG PLUS PREMIUM DM-i" -> img = R.drawable.byd_song_plus
                        "BYD SONG PLUS DM-i" -> img = R.drawable.byd_song_plus
                        "BYD KING DM-i" -> img = R.drawable.byd_king
                        "BYD SHARK" -> img = R.drawable.byd_shark
                        else -> {
                        }
                    }
                    Image(
                        painter = inspectionInfo.image?.let { painterResource(it) }
                            ?: painterResource(img),
                        contentDescription = inspectionInfo.chassi,
                        modifier = Modifier
                            .aspectRatio(16 / 9f)
                    )
                    //histórico do chassi
                    Text("Chassi:")
                    inspectionInfo.chassi?.let { Text(it, fontWeight = FontWeight.Bold) }
                    Image(
                        painter = painterResource(R.drawable.chassi_exemple),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("SOC %")
                    Text(text = inspectionInfo.soc.toString())
                    Image(
                        painter = painterResource(R.drawable.soc_example),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    //Ver como deixar os textos certinhos
                    Text("Pressão dos Pneus")
                    Text(text = inspectionInfo.DD?.toString() ?: "XX")
                    Text(text = inspectionInfo.DE?.toString() ?: "XX")
                    Text(text = inspectionInfo.TD?.toString() ?: "XX")
                    Text(text = inspectionInfo.TE?.toString() ?: "XX")
                    Image(
                        painter = painterResource(R.drawable.car_draw),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { showConfirmationDialog = true }){
                        Text("Está errado")
                    }

                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
    }
    if (showConfirmationDialog) {
        ConfirmationDialog(
            inspecInfo = inspectionInfo,
            onDismiss = { showConfirmationDialog = false },
            onChangeHistoricPDI =  { onChangeHistoricPDI(inspectionInfo)},
            onNewPdi = {onNewPdi(inspectionInfo) }
        )
    }
}



@Composable
@Preview(showBackground = true)
fun PreviewCarComponents() {
    val selectedInspectionInfo = remember { mutableStateOf<InspectionInfo?>(null) }

    // Main UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor) // Replace with your theme's background color
    ) {
        Text(
            text = "Car List",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            color = Color.White
        )

        InspectionInfoList(inspectionInfoList = fakeInspectionInfoLists) { car ->
            selectedInspectionInfo.value = car
        }
    }

    // Dialog UI
    selectedInspectionInfo.value?.let { car ->
        InpectionInfoModalDialog(
            inspectionInfo = car,
            onDismiss = { selectedInspectionInfo.value = null },
            onChangeHistoricPDI = {},
            onNewPdi = {}
        )
    }
}
