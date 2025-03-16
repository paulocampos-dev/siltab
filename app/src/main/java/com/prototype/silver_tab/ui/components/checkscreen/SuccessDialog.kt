//package com.prototype.silver_tab.ui.dialogs
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.window.Dialog
//import com.prototype.silver_tab.R
//import com.prototype.silver_tab.ui.theme.DarkGreen
//import com.prototype.silver_tab.utils.StringResources
//
//@Composable
//fun SuccessDialog(
//    show: Boolean,
//    onDismiss: () -> Unit,
//    chassiNumber: String,
//    strings: StringResources
//) {
//    if (show) {
//        Dialog(
//            onDismissRequest = { /* Do nothing to prevent dismiss on outside click */ }
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .background(Color.White, RoundedCornerShape(16.dp))
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(24.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    // Success icon
//                    Image(
//                        painter = painterResource(id = R.drawable.check_circle),
//                        contentDescription = "Success",
//                        modifier = Modifier.size(80.dp),
//                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(DarkGreen)
//                    )
//
//                    // Success title
//                    Text(
//                        text = strings.successPDI,
//                        style = MaterialTheme.typography.headlineSmall,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.Black,
//                        textAlign = TextAlign.Center
//                    )
//
//                    // Chassis information
//                    Text(
//                        text = "${strings.Vin}: $chassiNumber",
//                        style = MaterialTheme.typography.bodyLarge,
//                        color = Color.DarkGray,
//                        textAlign = TextAlign.Center
//                    )
//
//                    // Additional message
//                    Text(
//                        text = strings.successExtra,
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.Gray,
//                        textAlign = TextAlign.Center
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Close button
//                    Button(
//                        onClick = onDismiss,
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = DarkGreen
//                        )
//                    ) {
//                        Text(
//                            text = strings.close,
//                            fontSize = 16.sp,
//                            color = Color.White
//                        )
//                    }
//                }
//            }
//        }
//    }
//}