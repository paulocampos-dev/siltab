//package com.prototype.silver_tab.ui.components
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.prototype.silver_tab.R
//import com.prototype.silver_tab.data.models.InspectionInfo
//import com.prototype.silver_tab.utils.LocalStringResources
//import com.prototype.silver_tab.utils.formatRelativeDate
//
//
//@Composable
//fun InspectionInfoCard(
//    inspectionInfo: InspectionInfo,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val strings = LocalStringResources.current
//
//    // Function to get translated vehicle type
//    fun getTranslatedType(type: String?): String {
//        return when (type?.lowercase()) {
//            "híbrido", "hybrid", "hibrido" -> strings.vehicleTypeHybrid
//            "elétrico", "electric", "eletrico" -> strings.vehicleTypeElectric
//            else -> type ?: ""
//        }
//    }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//            .clickable { onClick() },
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
//    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            var img = R.drawable.pid_car
//            when (inspectionInfo.name) {
//                "BYD YUAN PLUS" -> img = R.drawable.byd_yuan_plus
//                "BYD TAN" -> img =  R.drawable.byd_tan
//                "BYD YUAN PRO" -> img = R.drawable.byd_yuan_pro
//                "BYD SEAL" -> img = R.drawable.pid_car
//                "BYD HAN" -> img = R.drawable.byd_han
//                "BYD DOLPHIN PLUS" -> img = R.drawable.byd_dolphin_plus
//                "BYD DOLPHIN" -> img = R.drawable.byd_dolphin
//                "BYD DOLPHIN MINI" -> img = R.drawable.byd_dolphin_mini
//                "BYD SONG PRO DM-i" -> img = R.drawable.byd_song_pro
//                "SONG PLUS PREMIUM DM-i" -> img = R.drawable.byd_song_premium
//                "BYD SONG PLUS DM-i" -> img = R.drawable.byd_song_premium
//                "BYD KING DM-i" -> img = R.drawable.byd_king
//                "BYD SHARK" -> img = R.drawable.byd_shark
//            }
//            Image(
//                painter = inspectionInfo.image?.let { painterResource(it) } ?: painterResource(img),
//                contentDescription = inspectionInfo.vin,
//                modifier = Modifier
//                    .size(64.dp)
//                    .clip(RoundedCornerShape(8.dp))
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Column {
//                inspectionInfo.vin?.let {
//                    Text(it, color = Color.White, fontWeight = FontWeight.Bold)
//                } ?: inspectionInfo.name?.let {Text(it, color = Color.White, fontWeight = FontWeight.Bold)}
//
//                // Use the translated type here
//                if (inspectionInfo.type != null) {
//                    Text(
//                        text = getTranslatedType(inspectionInfo.type),
//                        color = Color.Gray
//                    )
//                }
//
//                if (inspectionInfo.date != null) {
//                    Text(text = formatRelativeDate(inspectionInfo.date), color = Color.Gray)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun InspectionInfoList(inspectionInfoList: List<InspectionInfo>, onCarClicked: (InspectionInfo) -> Unit) {
//    LazyColumn(
//        modifier = Modifier.fillMaxSize(),
//        contentPadding = PaddingValues(vertical = 8.dp)
//    ) {
//        items(items = inspectionInfoList) { car ->
//            InspectionInfoCard(inspectionInfo = car, onClick = { onCarClicked(car) })
//        }
//    }
//}