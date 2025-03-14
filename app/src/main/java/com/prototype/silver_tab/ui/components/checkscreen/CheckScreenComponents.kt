//package com.prototype.silver_tab.ui.components.checkscreen
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.runtime.remember
//import androidx.compose.ui.focus.FocusRequester
//import androidx.compose.ui.focus.focusRequester
//import androidx.compose.ui.platform.LocalFocusManager
//import androidx.compose.ui.platform.LocalSoftwareKeyboardController
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.input.KeyboardType
//import com.prototype.silver_tab.R
//import com.prototype.silver_tab.data.models.InspectionInfo
//import com.prototype.silver_tab.utils.LocalStringResources
//
//@Composable
//fun VehicleInfoCard(
//    selectedInspectionInfo: InspectionInfo?,
//    modifier: Modifier = Modifier
//) {
//
//    val strings = LocalStringResources.current
//
//    ElevatedCard(
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 6.dp
//        ),
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Color.White
//        )
//    ){
//        Row(
//            modifier = Modifier.padding(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = selectedInspectionInfo?.name ?: "Unknown Car",
//                    style = MaterialTheme.typography.titleMedium,
//                    color = Color.Black,
//                    fontWeight = FontWeight.Bold
//                )
//                // Get localized vehicle type
//                val vehicleType = when (selectedInspectionInfo?.type?.lowercase()) {
//                    "híbrido", "hybrid" -> strings.vehicleTypeHybrid
//                    "elétrico", "electric" -> strings.vehicleTypeElectric
//                    else -> selectedInspectionInfo?.type ?: "Unknown Type"
//                }
//                Text(
//                    text = vehicleType,
//                    color = Color.Black,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//            Image(
//                painter = selectedInspectionInfo?.image?.let { painterResource(it) }
//                    ?: painterResource(id = R.drawable.ic_launcher_background),
//                contentDescription = "Car Image",
//                modifier = Modifier.size(80.dp)
//            )
//        }
//    }
//}
//
//@Composable
//fun TirePressureSection(
//    frontLeftPressure: String,
//    frontRightPressure: String,
//    rearLeftPressure: String,
//    rearRightPressure: String,
//    onFrontLeftChange: (String) -> Unit,
//    onFrontRightChange: (String) -> Unit,
//    onRearLeftChange: (String) -> Unit,
//    onRearRightChange: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//
//    // Gerenciadores de foco e teclado
//    val focusManager = LocalFocusManager.current
//    val keyboardController = LocalSoftwareKeyboardController.current
//
//    // Criando FocusRequesters para cada campo
//    val frontLeftFocusRequester = remember { FocusRequester() }
//    val frontRightFocusRequester = remember { FocusRequester() }
//    val rearLeftFocusRequester = remember { FocusRequester() }
//    val rearRightFocusRequester = remember { FocusRequester() }
//
//    Column(modifier = modifier.fillMaxWidth()) {
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            OutlinedTextField(
//                value = frontLeftPressure.removeSuffix(".0"),
//                onValueChange = { newValue ->
//                    // Permite somente até 2 dígitos e somente números
//                    if (newValue.length <= 2 && (newValue.isEmpty() || newValue.all { it.isDigit() })) {
//                        onFrontLeftChange(newValue)
//                        if (newValue.length == 2) {
//                            // Após 2 dígitos, direciona o foco para o próximo campo
//                            frontRightFocusRequester.requestFocus()
//                        }
//                    }
//                },
//                label = { Text("frontLeftTire", color = Color.White) },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Number,
//                    imeAction = ImeAction.Next
//                ),
//                singleLine = true,
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(end = 4.dp)
//                    .focusRequester(frontLeftFocusRequester),
//                colors = TextFieldDefaults.colors(
//                    focusedTextColor = Color.White,
//                    unfocusedTextColor = Color.White,
//                    cursorColor = Color.White,
//                    focusedContainerColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent,
//                    focusedLabelColor = Color.Gray,
//                    unfocusedLabelColor = Color.Gray,
//                    focusedIndicatorColor = Color.Gray,
//                    unfocusedIndicatorColor = Color.Gray,
//                    focusedPlaceholderColor = Color.Gray,
//                    unfocusedPlaceholderColor = Color.Gray
//                )
//            )
//            OutlinedTextField(
//                value = frontRightPressure.removeSuffix(".0"),
//                onValueChange = { newValue ->
//                    if (newValue.length <= 2 && (newValue.isEmpty() || newValue.all { it.isDigit() })) {
//                        onFrontRightChange(newValue)
//                        if (newValue.length == 2) {
//                            rearLeftFocusRequester.requestFocus()
//                        }
//                    }
//                },
//                label = { Text("frontRightTire", color = Color.White) },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Number,
//                    imeAction = ImeAction.Next
//                ),
//                singleLine = true,
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(start = 4.dp)
//                    .focusRequester(frontRightFocusRequester),
//                colors = TextFieldDefaults.colors(
//                    focusedTextColor = Color.White,
//                    unfocusedTextColor = Color.White,
//                    cursorColor = Color.White,
//                    focusedContainerColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent,
//                    focusedLabelColor = Color.Gray,
//                    unfocusedLabelColor = Color.Gray,
//                    focusedIndicatorColor = Color.Gray,
//                    unfocusedIndicatorColor = Color.Gray,
//                    focusedPlaceholderColor = Color.Gray,
//                    unfocusedPlaceholderColor = Color.Gray
//                )
//            )
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            OutlinedTextField(
//                value = rearLeftPressure.removeSuffix(".0"),
//                onValueChange = { newValue ->
//                    if (newValue.length <= 2 && (newValue.isEmpty() || newValue.all { it.isDigit() })) {
//                        onRearLeftChange(newValue)
//                        if (newValue.length == 2) {
//                            rearRightFocusRequester.requestFocus()
//                        }
//                    }
//                },
//                label = { Text("rearLeftTire", color = Color.White) },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Number,
//                    imeAction = ImeAction.Next
//                ),
//                singleLine = true,
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(end = 4.dp)
//                    .focusRequester(rearLeftFocusRequester),
//                colors = TextFieldDefaults.colors(
//                    focusedTextColor = Color.White,
//                    unfocusedTextColor = Color.White,
//                    cursorColor = Color.White,
//                    focusedContainerColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent,
//                    focusedLabelColor = Color.Gray,
//                    unfocusedLabelColor = Color.Gray,
//                    focusedIndicatorColor = Color.Gray,
//                    unfocusedIndicatorColor = Color.Gray,
//                    focusedPlaceholderColor = Color.Gray,
//                    unfocusedPlaceholderColor = Color.Gray
//                )
//            )
//            OutlinedTextField(
//                value = rearRightPressure.removeSuffix(".0"),
//                onValueChange = { newValue ->
//                    if (newValue.length <= 2 && (newValue.isEmpty() || newValue.all { it.isDigit() })) {
//                        onRearRightChange(newValue)
//                        if (newValue.length == 2) {
//                            // No último campo, remove o foco e oculta o teclado
//                            focusManager.clearFocus()
//                            keyboardController?.hide()
//                        }
//                    }
//                },
//                label = { Text("rearRightTire", color = Color.White) },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Number,
//                    imeAction = ImeAction.Done
//                ),
//                singleLine = true,
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(start = 4.dp)
//                    .focusRequester(rearRightFocusRequester),
//                colors = TextFieldDefaults.colors(
//                    focusedTextColor = Color.White,
//                    unfocusedTextColor = Color.White,
//                    cursorColor = Color.White,
//                    focusedContainerColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent,
//                    focusedLabelColor = Color.Gray,
//                    unfocusedLabelColor = Color.Gray,
//                    focusedIndicatorColor = Color.Gray,
//                    unfocusedIndicatorColor = Color.Gray,
//                    focusedPlaceholderColor = Color.Gray,
//                    unfocusedPlaceholderColor = Color.Gray
//                )
//            )
//        }
//    }
//}
//
//@Composable
//fun HybridCarSection(
//    isCarStarted: Boolean,
//    onCarStartedChange: (Boolean) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val strings = LocalStringResources.current
//
//    Column(modifier = modifier.fillMaxWidth()) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Checkbox(
//                checked = isCarStarted,
//                onCheckedChange = onCarStartedChange
//            )
//            Text(
//                text = strings.carStarted,
//                modifier = Modifier.padding(start = 8.dp),
//                color = Color.White
//            )
//        }
//    }
//}