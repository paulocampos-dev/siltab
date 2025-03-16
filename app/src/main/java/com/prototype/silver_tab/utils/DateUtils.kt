package com.prototype.silver_tab.utils

import com.prototype.silver_tab.language.StringResources
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

fun formatRelativeDate(
    dateStr: String,
    strings: StringResources
): String {
    try {
        // Try parsing both ISO datetime and date formats
        val date = try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateStr)
        } catch (e: Exception) {
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
            } catch (e: Exception) {
                return dateStr // Return original if parsing fails
            }
        } ?: return dateStr

        val today = Calendar.getInstance()
        val dateCalendar = Calendar.getInstance()
        dateCalendar.time = date

        // Calculate difference in days
        val diffInMillis = abs(today.timeInMillis - dateCalendar.timeInMillis)
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

        // Calculate months difference
        val yearDiff = today.get(Calendar.YEAR) - dateCalendar.get(Calendar.YEAR)
        val monthDiff = today.get(Calendar.MONTH) - dateCalendar.get(Calendar.MONTH)
        val totalMonthsDiff = yearDiff * 12 + monthDiff

        return when {
            // If more than 3 months ago, show formatted date
            totalMonthsDiff > 3 -> {
                SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss", Locale.getDefault()).format(date)
            }
            // If within last 3 months, show relative date
            diffInDays == 0 -> strings.today ?: "Today"
            diffInDays == 1 -> strings.yesterday ?: "Yesterday"
            else -> "$diffInDays ${strings.daysAgo}"
        }
    } catch (e: Exception) {
        return dateStr
    }
}


private fun monthsBetween(date1: Calendar, date2: Calendar): Int {
    val yearDiff = date2.get(Calendar.YEAR) - date1.get(Calendar.YEAR)
    val monthDiff = date2.get(Calendar.MONTH) - date1.get(Calendar.MONTH)
    return yearDiff * 12 + monthDiff
}