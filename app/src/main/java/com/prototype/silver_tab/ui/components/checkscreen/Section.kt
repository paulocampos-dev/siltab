package com.prototype.silver_tab.ui.components.checkscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.ui.components.help.HelpButton

@Composable
fun Section(
    title: String,
    modifier: Modifier = Modifier,
    showHelpModal: Boolean,
    showHelpIcon: Boolean = true,
    onShowHelpModalChange: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(vertical = 16.dp)) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            if (showHelpIcon) {
                HelpButton(onClick = { onShowHelpModalChange(true) })
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}
