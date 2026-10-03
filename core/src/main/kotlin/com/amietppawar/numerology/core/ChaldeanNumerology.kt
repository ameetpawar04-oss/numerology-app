package com.amietppawar.numerology.core

/**
 * Pure Kotlin Chaldean numerology engine with no Android dependencies.
 *
 * Letter mapping (the only mapping used throughout):
 * 1 = A, I, J, Q, Y
 * 2 = B, K, R
 * 3 = C, G, L, S
 * 4 = D, M, T
 * 5 = E, H, N, X
 * 6 = U, V, W
 * 7 = O, Z
 * 8 = F, P
 *
 * No letter is assigned 9. Spaces, dots, hyphens and non-letters are ignored.
 */

object ChaldeanNumerology {

    private val letterToNumber = mapOf(
        'A' to 1, 'I' to 1, 'J' to 1, 'Q' to 1, 'Y' to 1,
        'B' to 2, 'K' to 2, 'R' to 2,
        'C' to 3, 'G' to 3, 'L' to 3, 'S' to 3,
        'D' to 4, 'M' to 4, 'T' to 4,
        'E' to 5, 'H' to 5, 'N' to 5, 'X' to 5,
        'U' to 6, 'V' to 6, 'W' to 6,
        'O' to 7, 'Z' to 7,
        'F' to 8, 'P' to 8
    )

    /**
     * Calculate the name number from a string.
     * Ignores spaces, dots, hyphens and non-letter characters.
     * Returns the compound (total) and reduced (single digit) numbers.
     */
    fun calculateName(name: String): NameNumber {
        if (name.isBlank()) {
            return NameNumber(
                original = name,
                letters = emptyList(),
                compound = 0,
                reduced = 0
            )
        }

        val cleanName = name.uppercase()
        val letters = mutableListOf<LetterValue>()
        var total = 0

        for (char in cleanName) {
            val number = letterToNumber[char]
            if (number != null) {
                letters.add(LetterValue(char, number))
                total += number
            }
        }

        if (letters.isEmpty()) {
            return NameNumber(
                original = name,
                letters = emptyList(),
                compound = 0,
                reduced = 0
            )
        }

        return NameNumber(
            original = name,
            letters = letters,
            compound = total,
            reduced = reduce(total)
        )
    }

    /**
     * Calculate birth number from day of month (1-31).
     * Reduced to single digit.
     */
    fun calculateBirthNumber(day: Int): SingleNumber {
        val reduced = reduce(day)
        return SingleNumber(compound = day, reduced = reduced)
    }

    /**
     * Calculate destiny number from the full date of birth.
     * Every digit of the day, month and year is added together.
     * Example: 15 August 1990 = 1+5+8+1+9+9+0 = 33, reduced to 6.
     */
    fun calculateDestinyNumber(year: Int, month: Int, day: Int): SingleNumber {
        val total = digitSum(day) + digitSum(month) + digitSum(year)
        return SingleNumber(compound = total, reduced = reduce(total))
    }

    /**
     * Total for each word of a name, in order. Words without letters are skipped.
     */
    fun calculateWords(name: String): List<WordTotal> {
        return name.trim().split(Regex("\\s+"))
            .map { word -> WordTotal(word, calculateName(word).compound) }
            .filter { it.total > 0 }
    }

    /**
     * Add the digits of a number once (no repeated reduction).
     */
    fun digitSum(number: Int): Int {
        var sum = 0
        var n = if (number < 0) -number else number
        while (n > 0) {
            sum += n % 10
            n /= 10
        }
        return sum
    }

    /**
     * Reduce a number by summing its digits repeatedly until single digit.
     * Handles any positive integer.
     */
    fun reduce(number: Int): Int {
        if (number < 10) return number

        var sum = 0
        var n = number
        while (n > 0) {
            sum += n % 10
            n /= 10
        }

        return if (sum < 10) sum else reduce(sum)
    }

    /**
     * Generate a unique sigil identifier from a name's letter values.
     * Used for visually distinct representations. Deterministic—same name always produces same sigil.
     */
    fun generateSigilId(name: String): String {
        val nameNumber = calculateName(name)
        if (nameNumber.letters.isEmpty()) return "0"

        // Create a deterministic hash from letter values and positions
        var hash = 7L
        nameNumber.letters.forEachIndexed { index, letter ->
            hash = (hash * 31L + letter.value.toLong() * (index + 1).toLong()) % 1000000L
        }

        return hash.toString().padStart(6, '0')
    }
}

/**
 * Result of a name number calculation.
 */
data class NameNumber(
    val original: String,
    val letters: List<LetterValue>,
    val compound: Int,
    val reduced: Int
) {
    fun isEmpty() = letters.isEmpty()
}

/**
 * A single letter and its numeric value.
 */
data class LetterValue(
    val letter: Char,
    val value: Int
)

/**
 * Result of birth or destiny number calculation.
 */
data class SingleNumber(
    val compound: Int,
    val reduced: Int
)

/**
 * One word of a name and its compound total.
 */
data class WordTotal(
    val word: String,
    val total: Int
)
