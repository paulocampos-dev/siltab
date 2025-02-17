package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.R
import com.prototype.silver_tab.utils.Language
import com.prototype.silver_tab.utils.LocalStringResources
import com.prototype.silver_tab.utils.LocalizationManager

@Composable
fun LanguageMenuItem(
    language: String,
    flagResourceId: Int,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = flagResourceId),
                    contentDescription = "Flag for $language",
                    modifier = Modifier.size(24.dp)
                )
                Text(language)
            }
        },
        onClick = onClick
    )
}

@Composable
fun LanguageSelector(
    onLanguageSelected: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val strings = LocalStringResources.current
    val currentLanguage by LocalizationManager.currentLanguage.collectAsState()

    val currentFlagResource = when (currentLanguage) {
        Language.ENGLISH -> R.drawable.flag_us
        Language.PORTUGUESE -> R.drawable.flag_br
        Language.CHINESE -> R.drawable.flag_zh
        else -> {
            R.drawable.flag_br
        }
    }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = currentFlagResource),
                    contentDescription = "Current language flag",
                    modifier = Modifier.size(24.dp)
                )
                Text(strings.selectLanguage)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            LanguageMenuItem(
              language = "English",
              flagResourceId = R.drawable.flag_us,
              onClick = {
                  onLanguageSelected(Language.ENGLISH)
                  expanded = false
              }
            )
            LanguageMenuItem(
                language = "Português",
                flagResourceId = R.drawable.flag_br,
                onClick = {
                    onLanguageSelected(Language.PORTUGUESE)
                    expanded = false
                }
            )
            LanguageMenuItem(
              language = "中文",
              flagResourceId = R.drawable.flag_zh,
              onClick = {
                  onLanguageSelected(Language.CHINESE)
                  expanded = false
              }
            )
        }
    }
}