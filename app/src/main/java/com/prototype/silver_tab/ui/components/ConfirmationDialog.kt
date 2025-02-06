package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.data.models.InspectionInfo

@Composable
fun ConfirmationDialog (modifier: Modifier = Modifier,
                        onDismiss: () -> Unit,
                        onChangeHistoricPDI: (InspectionInfo) -> Unit,
                        inspecInfo : InspectionInfo,
                        onNewPdi : (InspectionInfo) -> Unit

){
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            surface = Color.White // Cor do fundo do diálogo
        )  //ver melhor como mudar a cor de alerts
    ){
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Alteração de PDI",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Você tem certeza que deseja mudar o PDI ou iniciar um novo?",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { onChangeHistoricPDI(inspecInfo) }) {
                            Text("Alterar PDI")
                        }
                        Button(onClick = {onNewPdi(inspecInfo)}) {
                            Text("Novo PDI")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(0.4f)
                ) {
                    Text("Fechar")
                }
            }
        )
    }
}