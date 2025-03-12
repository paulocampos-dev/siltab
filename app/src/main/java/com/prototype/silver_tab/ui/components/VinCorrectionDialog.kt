package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.StringResources

@Composable
fun VinCorrectionDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    originalVin: String,
    onSubmitNewVin: (String) -> Unit,
    strings: StringResources = LocalStringResources.current
) {
    if (show) {
        var newVin by remember { mutableStateOf("") }
        var vinError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Correct VIN Number",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display original VIN
                    Text(
                        text = "Original VIN:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = originalVin,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Input field for new VIN
                    OutlinedTextField(
                        value = newVin,
                        onValueChange = {
                            newVin = it
                            vinError = newVin.length != 17
                        },
                        label = { Text("New VIN Number") },
                        isError = vinError,
                        supportingText = {
                            if (vinError) {
                                Text(
                                    text = "VIN must be 17 characters",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newVin.length == 17) {
                            onSubmitNewVin(newVin)
                        } else {
                            vinError = true
                        }
                    },
                    enabled = newVin.isNotEmpty() && !vinError
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}