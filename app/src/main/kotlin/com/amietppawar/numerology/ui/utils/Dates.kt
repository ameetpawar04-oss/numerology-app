package com.amietppawar.numerology.ui.utils

import java.util.Calendar
import java.util.Locale

/** A checked calendar date. */
class BirthDate(val year: Int, val month: Int, val day: Int)

/** Date helpers that work on every supported Android version. */
object Dates {

    private val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    /** Returns the date if the three fields form a real, non-future date. */
    fun parse(dayText: String, monthText: String, yearText: String): BirthDate? {
        val day = dayText.trim().toIntOrNull() ?: return null
        val month = monthText.trim().toIntOrNull() ?: return null
        val year = yearText.trim().toIntOrNull() ?: return null

        val today = Calendar.getInstance()
        if (year < 1900 || year > today.get(Calendar.YEAR)) return null
        if (month < 1 || month > 12) return null

        val check = Calendar.getInstance()
        check.clear()
        check.set(year, month - 1, 1)
        val lastDay = check.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (day < 1 || day > lastDay) return null

        check.set(year, month - 1, day)
        if (check.after(today)) return null

        return BirthDate(year, month, day)
    }

    /** Stored form: YYYY-MM-DD. */
    fun toStorage(date: BirthDate): String {
        return String.format(Locale.US, "%04d-%02d-%02d", date.year, date.month, date.day)
    }

    fun fromStorage(text: String?): BirthDate? {
        if (text == null) return null
        val parts = text.split("-")
        if (parts.size != 3) return null
        return parse(parts[2], parts[1], parts[0])
    }

    /** For example "15 August 1990". */
    fun display(date: BirthDate): String {
        return date.day.toString() + " " + monthNames[date.month - 1] + " " + date.year
    }
}
