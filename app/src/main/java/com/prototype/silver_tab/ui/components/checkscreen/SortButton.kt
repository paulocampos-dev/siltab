package com.prototype.silver_tab.ui.components.checkscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.language.LocalStringResources
import com.prototype.silver_tab.viewmodels.SortOrder

@Composable
fun SortButton(
    sortOrder: SortOrder,
    onToggleSortOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStringResources.current

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Button(
            onClick = onToggleSortOrder,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF444444)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = when (sortOrder) {
                        SortOrder.NEWEST_FIRST -> Icons.Default.KeyboardArrowUp
                        SortOrder.OLDEST_FIRST -> Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = null,
                    tint = Color.White
                )

                Text(
                    text = when (sortOrder) {
                        SortOrder.NEWEST_FIRST -> strings.sortNewestFirst
                        SortOrder.OLDEST_FIRST -> strings.sortOldestFirst
                    },
                    color = Color.White
                )
            }
        }
    }
}