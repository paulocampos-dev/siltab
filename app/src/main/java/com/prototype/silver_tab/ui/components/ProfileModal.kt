package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.data.models.Profile

@Composable
fun ProfileModal(profile: Profile, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            surface = Color.Black // Cor do fundo do diálogo
        )
    ) {
        AlertDialog(
            modifier = Modifier.fillMaxHeight()
                .border(2.dp, Color.White, RoundedCornerShape(16.dp))
                .padding(2.dp),
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "BYD Profile",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White // Texto branco
                )
            },
            text = {
                Column(
                    modifier = Modifier.padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf(
                        "Email" to (profile.email ?: "namesurname@byd.com"),
                        "Nome" to (profile.name ?: "None"),
                        "Sobrenome" to (profile.surname ?: "None"),
                        "Job title" to (profile.job_title ?: "Job Title"),
                    ).forEach { (label, value) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$label:",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = value,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Fechar")
                }
            }
        )
    }
}