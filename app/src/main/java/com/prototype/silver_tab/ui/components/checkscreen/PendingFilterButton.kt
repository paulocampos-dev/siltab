package com.prototype.silver_tab.ui.components.checkscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.R
import com.prototype.silver_tab.language.LocalStringResources


@Composable
fun PendingFilterButton(
    showOnlyPending: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {

    val strings = LocalStringResources.current

    Button(
        onClick = onToggle,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (showOnlyPending) Color(0xFFAB3232) else Color(0xFF444444)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (showOnlyPending)
                        R.drawable.radio_button_checked
                    else
                        R.drawable.radio_button_unchecked
                ),
                contentDescription = "Filter Toggle Icon",
                tint = Color.White
            )
            Text(
                text = strings.showOnlyPending,
                color = Color.White
            )
        }
    }
}
