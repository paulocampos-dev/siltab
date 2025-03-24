package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prototype.silver_tab.data.models.InspectionInfo
import com.prototype.silver_tab.language.LocalStringResources
import com.prototype.silver_tab.utils.formatRelativeDate
import com.prototype.silver_tab.utils.getCarImageResource

@Composable
fun InspectionInfoCard(
    inspectionInfo: InspectionInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStringResources.current

    // Function to get translated vehicle type
    fun getTranslatedType(type: String?): String {
        return when (type?.lowercase()) {
            "híbrido", "hybrid", "hibrido" -> strings.vehicleTypeHybrid
            "elétrico", "electric", "eletrico" -> strings.vehicleTypeElectric
            else -> type ?: ""
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = if (inspectionInfo.pending == false){
            CardDefaults.cardColors(containerColor = Color.DarkGray)
        } else{
            CardDefaults.cardColors(containerColor = Color.Red)
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageResource = getCarImageResource(inspectionInfo.name)

            Image(
                painter = painterResource(id = imageResource),
                contentDescription = inspectionInfo.vin,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                // Show VIN prominently
                inspectionInfo.vin?.let {
                    Text(
                        text = it,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Car model name
                inspectionInfo.name?.let {
                    Text(
                        text = it,
                        color = Color.LightGray
                    )
                }

                // Translated vehicle type
                if (inspectionInfo.type != null) {
                    Text(
                        text = getTranslatedType(inspectionInfo.type),
                        color = Color.Gray
                    )
                }

                // Formatted relative date for better user understanding
                if (inspectionInfo.date != null) {
                    Text(
                        text = formatRelativeDate(inspectionInfo.date, strings),
                        color = Color.Gray
                    )
                }

            }
        }
    }
}
