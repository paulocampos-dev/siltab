package com.prototype.silver_tab.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.prototype.silver_tab.utils.StringResources

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
            title = { Text(text = strings.duplicateVin) },
            text = { Text(text = strings.duplicateVinMessage) },
            confirmButton = {
                Button(onClick = onFindInHistory) {
                    Text(text = strings.close)
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