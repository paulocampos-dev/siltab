package com.prototype.silver_tab.utils

import com.prototype.silver_tab.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun chooseImage(model: String): Int {
    return when (model) {
        "BYD YUAN PLUS" -> R.drawable.byd_yuan_plus
        "BYD TAN" -> R.drawable.byd_tan
        "BYD YUAN PRO" -> R.drawable.byd_yuan_pro
        "BYD HAN" -> R.drawable.byd_han
        else -> R.drawable.pid_car
    }
}

fun parseDate(dateString: String?): LocalDateTime {
    return try {
        dateString?.let { LocalDateTime.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) }
            ?: LocalDateTime.MIN
    } catch (e: Exception) {
        LocalDateTime.MIN
    }
}
