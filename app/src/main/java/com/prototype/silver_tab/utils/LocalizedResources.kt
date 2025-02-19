// LocalizedResources.kt
package com.prototype.silver_tab.utils

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.prototype.silver_tab.R

object LocalizedDrawables {
    val pdiButton = mapOf(
        Language.ENGLISH to R.drawable.pdi_button_en,
        Language.PORTUGUESE to R.drawable.pdi_button_pt,
        Language.CHINESE to R.drawable.pdi_button_zh
    )

    @Composable
    fun getLocalizedDrawable(drawableMap: Map<Language, Int>): Int {
        val currentLanguage by LocalizationManager.currentLanguage.collectAsState()
        return drawableMap[currentLanguage] ?: drawableMap[Language.ENGLISH] ?: drawableMap.values.first()
    }
}

// Usage example:
@Composable
fun LocalizedImage(
    drawableMap: Map<Language, Int>,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val resourceId = LocalizedDrawables.getLocalizedDrawable(drawableMap)
    Image(
        painter = painterResource(resourceId),
        contentDescription = contentDescription,
        modifier = modifier
    )
}