package com.prototype.silver_tab.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

fun formatRelativeDate(dateStr: String): String {
    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateStr) ?: return dateStr
        val today = Calendar.getInstance()
        val dateCalendar = Calendar.getInstance()
        dateCalendar.time = date

        // Calculate difference in days
        val diffInMillis = abs(today.timeInMillis - dateCalendar.timeInMillis)
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

        // Calculate months difference
        val months = monthsBetween(dateCalendar, today)

        return when {
            // If more than 3 months, return the original date
            months > 3 -> dateStr

            // If within last 3 months, return days ago
            else -> when (diffInDays) {
                0 -> "Hoje"
                1 -> "Ontem"
                else -> "Há $diffInDays dias"
            }
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