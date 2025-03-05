package com.prototype.silver_tab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.prototype.silver_tab.R
import com.prototype.silver_tab.utils.StringResources

@Composable
fun HelpModal(
    onDismiss: () -> Unit,
    img: Int?,
    type: String,
    strings: StringResources
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(

                    text = if (type == "híbrido") {
                        strings.helpTitle + " " + strings.vehicleTypeHybrid.uppercase()
                    } else {
                        strings.helpTitle + " ${type.uppercase()}"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = when (type) {
                        "chassi" -> strings.chassisHelp
                        "pneu" -> strings.tireHelp
                        "híbrido" -> strings.hybridHelp
                        "12vbattery" -> strings.battery12vHelp
                        else -> strings.socHelp
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                if( img != null) {
                    Spacer(Modifier.padding(8.dp))

                    Image(
                        painter = painterResource(id = img),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.padding(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2))
                ) {
                    Text(strings.understood, color = Color.White)
                }
            }
        }
    }
}



