package com.prototype.silver_tab.ui.components.checkscreen

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.prototype.silver_tab.utils.StringResources

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
