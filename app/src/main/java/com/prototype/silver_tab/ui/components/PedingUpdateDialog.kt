package com.prototype.silver_tab.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.language.LocalStringResources
import com.prototype.silver_tab.language.StringResources
import com.prototype.silver_tab.utils.logTimber
import com.prototype.silver_tab.viewmodels.InspectionDetailsViewModel
import kotlinx.coroutines.delay

@Composable
fun PendingUpdateDialog(    //TODO
    show: Boolean,
    onDismiss: () -> Unit,
    originalSoc: Float?,
    pdiId: Int?,
    onSubmitPendingUpdate: (Float) -> Unit,
    viewModel: InspectionDetailsViewModel,
    strings: StringResources = LocalStringResources.current
) {
    val context = LocalContext.current

    if (show) {
        var newSocText by remember { mutableStateOf("") }
        var newSoc by remember { mutableStateOf(0f) }
        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()
        val success by viewModel.success.collectAsState()

        // Track if we've already processed the success state
        var hasProcessedSuccess by remember { mutableStateOf(false) }

        // Handle success state changes
        LaunchedEffect(success) {
            if (success != null && !hasProcessedSuccess) {
                // Mark as processed to prevent multiple callbacks
                hasProcessedSuccess = true

                // Log the success value for debugging
                logTimber("PendingUpdate", "Success message: $success")

                // Wait a moment to ensure the API call has completed
                delay(500)

                // Submit the new SOC (which will be used in parent dialog)
                onSubmitPendingUpdate(newSoc)

                // Clear the success message after handling it
                viewModel.clearSuccessMessage()
            }
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Resolve Pending PDI",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display Before Update
                    Text(
                        text = "Soc:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = originalSoc.toString(),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Input field for new VIN
                    OutlinedTextField(
                        value = newSocText,
                        onValueChange = {
                            newSocText =it
                            val parsed = it.toFloatOrNull()
                            if (parsed != null) {
                                newSoc = parsed
                            }
                        },
                        label = { Text("New SOC percentage") },
                        supportingText = {

                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )

                    // Loading indicator
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .size(24.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                            // Call the viewModel to submit Uptade
                            viewModel.submitPendingUpdate(pdiId, newSoc)

                            onDismiss()
                    },
                    enabled = newSoc<=100,  //todo restrição mínma para enviar
                    colors = ButtonDefaults.buttonColors(
                        // Use a slightly different color when processing to indicate activity
                        containerColor = if (isLoading) Color.Gray else MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isLoading) {
                        // Show a small loading indicator inside the button
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Processing...")
                    } else {
                        Text("Submit")
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    enabled = !isLoading
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}