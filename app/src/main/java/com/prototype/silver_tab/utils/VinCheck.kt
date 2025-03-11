package com.prototype.silver_tab.utils

fun isValidVIN(vin: String): Boolean {
    // VIN must be exactly 17 characters.
    if (vin.length != 17) return false
    // Disallow the characters I, O, and Q (upper or lower case)
    if (vin.any { it.uppercaseChar() in listOf('I', 'O', 'Q') }) return false

    // Transliteration values for letters (digits remain the same)
    val transliteration = mapOf(
        'A' to 1, 'B' to 2, 'C' to 3, 'D' to 4, 'E' to 5, 'F' to 6, 'G' to 7, 'H' to 8,
        'J' to 1, 'K' to 2, 'L' to 3, 'M' to 4, 'N' to 5, 'P' to 7, 'R' to 9,
        'S' to 2, 'T' to 3, 'U' to 4, 'V' to 5, 'W' to 6, 'X' to 7, 'Y' to 8, 'Z' to 9
    )
    // Weight factors for each position
    val weights = listOf(8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2)

    var sum = 0
    for (i in vin.indices) {
        val c = vin[i].uppercaseChar()
        val value = if (c.isDigit()) c.digitToInt() else transliteration[c] ?: 0
        sum += value * weights[i]
    }
    val remainder = sum % 11
    val expectedCheckDigit = if (remainder == 10) 'X' else remainder.toString().first()
    // The 9th character (index 8) should match the expected check digit.
    return vin[8].uppercaseChar() == expectedCheckDigit
}
