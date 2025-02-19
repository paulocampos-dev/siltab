package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.sp
import com.prototype.silver_tab.R
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.data.models.fakeInspectionInfoLists
import com.prototype.silver_tab.ui.theme.BackgroundColor
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.formatRelativeDate



@Composable
fun InspectionInfoCard(
    inspectionInfo: InspectionInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStringResources.current

    // Function to get translated vehicle type
    fun getTranslatedType(type: String?): String {
        return when (type?.lowercase()) {
            "híbrido", "hybrid", "hibrido" -> strings.vehicleTypeHybrid
            "elétrico", "electric", "eletrico" -> strings.vehicleTypeElectric
            else -> type ?: ""
        }
    }

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

                // Use the translated type here
                if (inspectionInfo.type != null) {
                    Text(
                        text = getTranslatedType(inspectionInfo.type),
                        color = Color.Gray
                    )
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
    AlertDialog(
        modifier = Modifier.fillMaxHeight(),
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = { Text(text = "Detalhes do Último PDI", fontWeight = FontWeight.Bold, color = Color.Black) },
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
                    }
                    Image(
                        painter = inspectionInfo.image?.let { painterResource(it) }
                            ?: painterResource(img),
                        contentDescription = inspectionInfo.chassi,
                        modifier = Modifier
                            .aspectRatio(16 / 9f)
                    )
                    //histórico do chassi
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Chassi:", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))

                        inspectionInfo.chassi?.let { Text(it, color = Color.Black) }

                        Spacer(modifier = Modifier.height(8.dp))

                        Image(
                            painter = painterResource(R.drawable.chassi_exemple),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }


                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SOC %", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = inspectionInfo.soc.toString(), color = Color.Black)

                        Spacer(modifier = Modifier.height(8.dp))


                        Image(
                            painter = painterResource(R.drawable.soc_example),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    //Ver como deixar os textos certinhos
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pressão dos Pneus", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("DE")
                                Text(text = "${inspectionInfo.DE?.toString() ?: "XX"} PSI", color = Color.Black)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("DD")
                                Text(text = "${inspectionInfo.DD?.toString() ?: "XX"} PSI", color = Color.Black)
                            }
                        }

                        Image(
                            painter = painterResource(R.drawable.car_draw),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TE")
                                Text(text = "${inspectionInfo.TE?.toString() ?: "XX"} PSI", color = Color.Black)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TD")
                                Text(text = "${inspectionInfo.TD?.toString() ?: "XX"} PSI", color = Color.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showConfirmationDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Está errado", color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) {
                Text("Fechar", color = Color.White)
            }
        },
    )
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
