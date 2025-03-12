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
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.StringResources

@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onChangeHistoricPDI: (InspectionInfo) -> Unit,
    inspecInfo: InspectionInfo,
    onNewPdi: (InspectionInfo) -> Unit,
    onWrongVinSelected: (InspectionInfo) -> Unit,
    strings: StringResources = LocalStringResources.current
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            surface = Color.White
        )
    ) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "What information is wrong?",
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
                        text = "Please select what type of information is incorrect:",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // VIN number correction button
                    Button(
                        onClick = { onWrongVinSelected(inspecInfo) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("VIN Number")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // PDI information correction button
                    Button(
                        onClick = { onChangeHistoricPDI(inspecInfo) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("PDI Information")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(0.4f)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}