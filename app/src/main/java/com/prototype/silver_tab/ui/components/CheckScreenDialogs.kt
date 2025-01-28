package com.prototype.silver_tab.ui.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun CancelDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Cancelar?") },
            text = { Text("Tem certeza que deseja cancelar? Todos os dados preenchidos até agora serão perdidos.") },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Sim, cancelar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Não, voltar")
                }
            }
        )
    }
}

@Composable
fun FinishDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Concluir?") },
            text = { Text("O processo PID será encerrado e você não poderá alterar as informações depois. Tem certeza que quer concluir?") },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Concluir")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}