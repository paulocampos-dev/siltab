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
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.language.LocalStringResources
import kotlinx.coroutines.flow.map

@Composable
fun ProfileModal(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStringResources.current

    val username by remember {
        SilverTabApplication.authRepository.authState.map { it?.username ?: "" }
    }.collectAsState(initial = "")

    val email by remember {
        SilverTabApplication.authRepository.authState.map { it?.email ?: "" }
    }.collectAsState(initial = "")

    val role by remember {
        SilverTabApplication.authRepository.authState.map { it?.role }
    }.collectAsState(initial = null)

    val roleName by remember {
        SilverTabApplication.authRepository.authState.map { it?.roleName ?: "" }
    }.collectAsState(initial = "")

    val position by remember {
        SilverTabApplication.authRepository.authState.map { it?.position }
    }.collectAsState(initial = null)

    val positionName by remember {
        SilverTabApplication.authRepository.authState.map { it?.positionName ?: "" }
    }.collectAsState(initial = "")

    val entityAuthority by remember {
        SilverTabApplication.authRepository.authState.map { it?.userEntityAuthority ?: "" }
    }.collectAsState(initial = "")

    val commercialAccess by remember {
        SilverTabApplication.authRepository.authState.map { it?.hasCommercialPolicyAccess ?: "" }
    }.collectAsState(initial = "")


    AlertDialog(
        modifier = Modifier
            .fillMaxHeight(0.8f)
            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
            .padding(2.dp),
        onDismissRequest = onDismiss,
        containerColor = Color.Black,
        title = {
            Text(
//                text = strings.profileTitle,
                text = "profile title",
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
//                ProfileField(label = strings.profileEmail, value = email)
//                ProfileField(label = strings.profileUsername, value = username)
//                ProfileField(label = strings.profileRole, value = roleName)
//                ProfileField(label = strings.profilePosition, value = positionName)
//                ProfileField(label = strings.profileEntity, value = entityAuthority)
                ProfileField(label = "profile", value = email)
                ProfileField(label = "username", value = username)
                ProfileField(label = "profilerole", value = roleName)
                ProfileField(label = "position", value = positionName)
                ProfileField(label = "entity", value = entityAuthority)
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
//                Text(strings.close)
                Text("close")
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