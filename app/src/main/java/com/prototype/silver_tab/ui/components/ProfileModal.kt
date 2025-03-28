package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.prototype.silver_tab.language.LocalStringResources
import com.prototype.silver_tab.viewmodels.ProfileViewModel

@Composable
fun ProfileModal(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val strings = LocalStringResources.current

    val username by viewModel.username.collectAsState()
    val email by viewModel.email.collectAsState()
    val roleName by viewModel.roleName.collectAsState()
    val positionName by viewModel.positionName.collectAsState()
    val entityAuthority by viewModel.entityAuthority.collectAsState()
    val commercialAccess by viewModel.commercialPolicyAccess.collectAsState()

    AlertDialog(
        modifier = Modifier
            .fillMaxHeight(0.8f)
            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
            .padding(2.dp),
        onDismissRequest = onDismiss,
        containerColor = Color.Black,
        title = {
            Text(
                text = strings.profileTitle ?: "Profile",
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
                ProfileField(label = strings.profileEmail ?: "Email", value = email)
                ProfileField(label = strings.profileUsername ?: "Username", value = username)
                ProfileField(label = strings.profileRole ?: "Role", value = roleName)
                ProfileField(label = strings.profilePosition ?: "Position", value = positionName)
                ProfileField(label = strings.profileEntity ?: "Entity", value = entityAuthority)
//                ProfileField(label = strings.profileCommercialAccess ?: "Commercial Access", value = commercialAccess)
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
                Text(strings.close ?: "Close")
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