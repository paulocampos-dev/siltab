package com.prototype.silver_tab.ui.components.checkscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.prototype.silver_tab.R
import com.prototype.silver_tab.language.LocalStringResources
import com.prototype.silver_tab.language.StringResources

@Composable
fun CancelDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    strings: StringResources
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.cancel) },
        text = { Text(strings.cancelConfirmationMessage) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(strings.cancelConfirmation)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.no)
            }
        }
    )
}

@Composable
fun FinishDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    strings: StringResources
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.finishPdi) },
        text = { Text(strings.finishConfirmationMessage) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(strings.finishConfirmation)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.no)
            }
        }
    )
}

@Composable
fun SuccessDialog(
    show: Boolean,
    message: String?,
    vin: String,
    isCorrection: Boolean = false,
    onDismiss: () -> Unit,
    strings: StringResources = LocalStringResources.current
) {
    if (show) {
        Dialog(
            onDismissRequest = { /* Do nothing to prevent dismiss on outside click */ }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Success icon
                    Image(
                        painter = painterResource(id = R.drawable.check_circle),
                        contentDescription = "Success",
                        modifier = Modifier.size(80.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                            if (isCorrection) Color.Magenta else Color.Green
                        )
                    )

                    // Success title
                    Text(
                        text = if (isCorrection)
                            strings.successPdiUpdated
                        else strings.successPDI,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    // Chassis information
                    Text(
                        text = "${strings.vin}: $vin",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )

                    // Custom message or default
                    Text(
                        text = message ?: if (isCorrection)
                            strings.pdiUpdateSuccess
                        else strings.successExtra,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Close button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCorrection) Color.Magenta else Color.Green
                        )
                    ) {
                        Text(
                            text = strings.close,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun DuplicateVinDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onFindInHistory: () -> Unit,
    strings: StringResources
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = strings.duplicateVin)},
            text = { Text(text = strings.duplicateVinMessage)},
            confirmButton = {
                Button(onClick = onFindInHistory) {
//                    Text(text = strings.findInHistory ?: "Find in History")
                    Text(text = "Find in History")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text(text = strings.cancel)
                }
            }
        )
    }
}