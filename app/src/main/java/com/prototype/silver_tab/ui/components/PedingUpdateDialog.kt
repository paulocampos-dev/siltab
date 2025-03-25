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
    originalVin: String,
    onSubmitNewVin: (String) -> Unit,
    viewModel: InspectionDetailsViewModel,
    strings: StringResources = LocalStringResources.current
) {
    val context = LocalContext.current

    if (show) {
        var newVin by remember { mutableStateOf("") }
        var vinError by remember { mutableStateOf(false) }
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
                logTimber("VinCorrectionDialog", "Success message: $success")

                // Wait a moment to ensure the API call has completed
                delay(500)

                // Submit the new VIN (which will be used in parent dialog)
                onSubmitNewVin(newVin)

                // Clear the success message after handling it
                viewModel.clearSuccessMessage()
            }
        }

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
                            newVin = it.uppercase()
                            vinError = newVin.length != 17
                        },
                        label = { Text("New VIN Number") },
                        isError = vinError || error != null,
                        supportingText = {
                            when {
                                vinError -> {
                                    Text(
                                        text = "VIN must be 17 characters",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                error != null -> {
                                    Text(
                                        text = error!!,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
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
                        if (newVin.length == 17) {
                            // Reset processed flag when submitting a new correction
                            hasProcessedSuccess = false

                            // Disable error state if it was previously shown
                            vinError = false

                            // Call the viewModel to submit VIN correction
                            viewModel.submitVinCorrection(originalVin, newVin)

                            onDismiss()
                        } else {
                            vinError = true
                        }
                    },
                    enabled = newVin.isNotEmpty() && !vinError && !isLoading,
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