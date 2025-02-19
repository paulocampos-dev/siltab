package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.SilverTabApplication.Companion.userPreferences
import com.prototype.silver_tab.utils.LocalStringResources

@Composable
fun ProfileModal(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStringResources.current

    // Collect user data
    val username by userPreferences.username.collectAsState(initial = "")
    val email by userPreferences.email.collectAsState(initial = "")
    val role by userPreferences.role.collectAsState(initial = null)
    val roleName by userPreferences.roleName.collectAsState(initial = "")
    val position by userPreferences.position.collectAsState(initial = null)
    val positionName by userPreferences.positionName.collectAsState(initial = "")
    val entityAuthority by userPreferences.userEntityAuthority.collectAsState(initial = "")
    val commercialAccess by userPreferences.hasCommercialPolicyAccess.collectAsState(initial = "")

    AlertDialog(
        modifier = Modifier
            .fillMaxHeight(0.8f)
            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
            .padding(2.dp),
        onDismissRequest = onDismiss,
        containerColor = Color.Black,
        title = {
            Text(
                text = strings.profileTitle,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileField(label = strings.profileEmail, value = email)
                ProfileField(label = strings.profileUsername, value = username)
                ProfileField(label = strings.profileRole, value = roleName)
                ProfileField(label = strings.profilePosition, value = positionName)
                ProfileField(label = strings.profileEntity, value = entityAuthority)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.close)
            }
        }
    )
}

@Composable
private fun ProfileField(
    label: String,
    value: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$label:",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value ?: "N/A",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
            )
        }
    }
}