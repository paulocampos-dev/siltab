package com.prototype.silver_tab.ui.components.checkscreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.utils.LocalStringResources

@Composable
fun AdditionalInfoSection(
    additionalInfo: String,
    onAdditionalInfoChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = additionalInfo,
        onValueChange = onAdditionalInfoChange,
        label = { Text(text = LocalStringResources.current.additionalInfo , color = Color.White) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(100.dp),
        maxLines = 5,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray,
            focusedIndicatorColor = Color.Gray,
            unfocusedIndicatorColor = Color.Gray,
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray
        )
    )
}
