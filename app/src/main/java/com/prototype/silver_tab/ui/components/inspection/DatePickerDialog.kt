package com.prototype.silver_tab.ui.components.inspection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.prototype.silver_tab.language.LocalStringResources
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoldDatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val strings = LocalStringResources.current
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Sale Date",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                DatePicker(
                    state = datePickerState,
                    title = null,
                    headline = null,
                    showModeToggle = false,
                    colors = DatePickerDefaults.colors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        headlineContentColor = Color.Black,
                        weekdayContentColor = Color.Gray,
                        subheadContentColor = Color.DarkGray,
                        yearContentColor = Color.Black,
                        currentYearContentColor = Color.Blue,
                        selectedYearContainerColor = Color.Blue,
                        selectedYearContentColor = Color.White,
                        dayContentColor = Color.Black,
                        selectedDayContainerColor = Color.Blue,
                        selectedDayContentColor = Color.White,
                        todayContentColor = Color.Blue,
                        todayDateBorderColor = Color.Blue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { dateMillis ->
                                val instant = Instant.ofEpochMilli(dateMillis)
                                val localDateTime = LocalDateTime.ofInstant(
                                    instant,
                                    ZoneId.systemDefault()
                                )
                                val formattedDate = localDateTime.format(
                                    DateTimeFormatter.ISO_DATE_TIME
                                )
                                onDateSelected(formattedDate)
                            }
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}