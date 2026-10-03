package com.amietppawar.numerology

import com.amietppawar.numerology.core.ChaldeanNumerology
import java.util.Calendar

/**
 * The number of the day: every digit of today's date added together and
 * reduced to a single digit. The same for everyone on a given day.
 */
object DailyNumber {

    fun today(): Int {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        return ChaldeanNumerology.calculateDestinyNumber(year, month, day).reduced
    }

    fun meaning(number: Int): String {
        return AppConfig.NUMBER_MEANINGS[number] ?: ""
    }
}
