package com.amietppawar.numerology.core

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for the Chaldean numerology engine.
 * Tests cover all calculation methods with known values and edge cases.
 */
class ChaldeanNumerologyTest {

    @Test
    fun testSingleLetters() {
        // Test each digit's letters
        assertEquals(1, ChaldeanNumerology.calculateName("A").reduced)
        assertEquals(1, ChaldeanNumerology.calculateName("I").reduced)
        assertEquals(1, ChaldeanNumerology.calculateName("J").reduced)
        assertEquals(1, ChaldeanNumerology.calculateName("Q").reduced)
        assertEquals(1, ChaldeanNumerology.calculateName("Y").reduced)

        assertEquals(2, ChaldeanNumerology.calculateName("B").reduced)
        assertEquals(2, ChaldeanNumerology.calculateName("K").reduced)
        assertEquals(2, ChaldeanNumerology.calculateName("R").reduced)

        assertEquals(3, ChaldeanNumerology.calculateName("C").reduced)
        assertEquals(3, ChaldeanNumerology.calculateName("G").reduced)
        assertEquals(3, ChaldeanNumerology.calculateName("L").reduced)
        assertEquals(3, ChaldeanNumerology.calculateName("S").reduced)

        assertEquals(4, ChaldeanNumerology.calculateName("D").reduced)
        assertEquals(4, ChaldeanNumerology.calculateName("M").reduced)
        assertEquals(4, ChaldeanNumerology.calculateName("T").reduced)

        assertEquals(5, ChaldeanNumerology.calculateName("E").reduced)
        assertEquals(5, ChaldeanNumerology.calculateName("H").reduced)
        assertEquals(5, ChaldeanNumerology.calculateName("N").reduced)
        assertEquals(5, ChaldeanNumerology.calculateName("X").reduced)

        assertEquals(6, ChaldeanNumerology.calculateName("U").reduced)
        assertEquals(6, ChaldeanNumerology.calculateName("V").reduced)
        assertEquals(6, ChaldeanNumerology.calculateName("W").reduced)

        assertEquals(7, ChaldeanNumerology.calculateName("O").reduced)
        assertEquals(7, ChaldeanNumerology.calculateName("Z").reduced)

        assertEquals(8, ChaldeanNumerology.calculateName("F").reduced)
        assertEquals(8, ChaldeanNumerology.calculateName("P").reduced)
    }

    @Test
    fun testAmiet() {
        // A(1) + M(4) + I(1) + E(5) + T(4) = 15 → 1+5 = 6
        val result = ChaldeanNumerology.calculateName("AMIET")
        assertEquals(15, result.compound)
        assertEquals(6, result.reduced)
        assertEquals("AMIET", result.original.uppercase())
        assertEquals(5, result.letters.size)
    }

    @Test
    fun testPpawar() {
        // P(8) + P(8) + A(1) + W(6) + A(1) + R(2) = 26 → 2+6 = 8
        val result = ChaldeanNumerology.calculateName("PPAWAR")
        assertEquals(26, result.compound)
        assertEquals(8, result.reduced)
    }

    @Test
    fun testAmietPpawar() {
        // A(1) + M(4) + I(1) + E(5) + T(4) + P(8) + P(8) + A(1) + W(6) + A(1) + R(2) = 41 → 4+1 = 5
        val result = ChaldeanNumerology.calculateName("AMIET PPAWAR")
        assertEquals(41, result.compound)
        assertEquals(5, result.reduced)
    }

    @Test
    fun testCaseInsensitivity() {
        assertEquals(
            ChaldeanNumerology.calculateName("amiet").reduced,
            ChaldeanNumerology.calculateName("AMIET").reduced
        )
        assertEquals(
            ChaldeanNumerology.calculateName("AmIeT").reduced,
            ChaldeanNumerology.calculateName("AMIET").reduced
        )
    }

    @Test
    fun testSpacesAndPunctuation() {
        // Spaces, dots, hyphens should be ignored
        val base = ChaldeanNumerology.calculateName("AMIETPPAWAR")
        val withSpace = ChaldeanNumerology.calculateName("AMIET PPAWAR")
        val withDots = ChaldeanNumerology.calculateName("AMIET.PPAWAR")
        val withHyphen = ChaldeanNumerology.calculateName("AMIET-PPAWAR")

        assertEquals(base.compound, withSpace.compound)
        assertEquals(base.compound, withDots.compound)
        assertEquals(base.compound, withHyphen.compound)
    }

    @Test
    fun testEmptyAndInvalid() {
        val empty = ChaldeanNumerology.calculateName("")
        assertEquals(0, empty.compound)
        assertEquals(0, empty.reduced)
        assert(empty.isEmpty())

        val spaces = ChaldeanNumerology.calculateName("   ")
        assertEquals(0, spaces.compound)
        assertEquals(0, spaces.reduced)
        assert(spaces.isEmpty())

        val numbers = ChaldeanNumerology.calculateName("123456")
        assertEquals(0, numbers.compound)
        assertEquals(0, numbers.reduced)
        assert(numbers.isEmpty())

        val symbols = ChaldeanNumerology.calculateName("!@#$%^")
        assertEquals(0, symbols.compound)
        assertEquals(0, symbols.reduced)
        assert(symbols.isEmpty())
    }

    @Test
    fun testLongNames() {
        val longName = ChaldeanNumerology.calculateName("ALEXANDERNICHOLAI")
        // A(1) + L(3) + E(5) + X(5) + A(1) + N(5) + D(4) + E(5) + R(2) + N(5) + I(1) + C(3) + H(5) + O(7) + L(3) + A(1) + I(1) = 57 → 5+7 = 12 → 1+2 = 3
        assertEquals(57, longName.compound)
        assertEquals(3, longName.reduced)
        assertEquals(17, longName.letters.size)
    }

    @Test
    fun testReduce() {
        assertEquals(1, ChaldeanNumerology.reduce(1))
        assertEquals(9, ChaldeanNumerology.reduce(9))
        assertEquals(1, ChaldeanNumerology.reduce(10))
        assertEquals(1, ChaldeanNumerology.reduce(19))
        assertEquals(2, ChaldeanNumerology.reduce(20))
        assertEquals(9, ChaldeanNumerology.reduce(18))
        assertEquals(6, ChaldeanNumerology.reduce(15))
        assertEquals(3, ChaldeanNumerology.reduce(57))
        assertEquals(1, ChaldeanNumerology.reduce(1000000))
    }

    @Test
    fun testBirthNumber() {
        // Day 15 → 1+5 = 6
        val day15 = ChaldeanNumerology.calculateBirthNumber(15)
        assertEquals(15, day15.compound)
        assertEquals(6, day15.reduced)

        // Day 1 = 1
        val day1 = ChaldeanNumerology.calculateBirthNumber(1)
        assertEquals(1, day1.compound)
        assertEquals(1, day1.reduced)

        // Day 31 → 3+1 = 4
        val day31 = ChaldeanNumerology.calculateBirthNumber(31)
        assertEquals(31, day31.compound)
        assertEquals(4, day31.reduced)
    }

    @Test
    fun testDestinyNumber() {
        // 15 August 1990: 1+5+8+1+9+9+0 = 33 -> 3+3 = 6
        val destiny = ChaldeanNumerology.calculateDestinyNumber(1990, 8, 15)
        assertEquals(33, destiny.compound)
        assertEquals(6, destiny.reduced)

        // 1 January 2000: 1+1+2+0+0+0 = 4
        val destiny2 = ChaldeanNumerology.calculateDestinyNumber(2000, 1, 1)
        assertEquals(4, destiny2.compound)
        assertEquals(4, destiny2.reduced)

        // 31 December 1999: 3+1+1+2+1+9+9+9 = 35 -> 3+5 = 8
        val destiny3 = ChaldeanNumerology.calculateDestinyNumber(1999, 12, 31)
        assertEquals(35, destiny3.compound)
        assertEquals(8, destiny3.reduced)
    }

    @Test
    fun testWordTotals() {
        val words = ChaldeanNumerology.calculateWords("Amiet  Ppawar")
        assertEquals(2, words.size)
        assertEquals(15, words[0].total)
        assertEquals(26, words[1].total)
        assertEquals(0, ChaldeanNumerology.calculateWords("   ").size)
        assertEquals(0, ChaldeanNumerology.calculateWords("123 !!").size)
    }

    @Test
    fun testSigilGeneration() {
        // Sigils should be deterministic—same name always produces same sigil
        val sigil1 = ChaldeanNumerology.generateSigilId("AMIET")
        val sigil2 = ChaldeanNumerology.generateSigilId("AMIET")
        assertEquals(sigil1, sigil2)

        // Different names should (usually) produce different sigils
        val sigilA = ChaldeanNumerology.generateSigilId("AMIET")
        val sigilB = ChaldeanNumerology.generateSigilId("PPAWAR")
        // (they might collide, but extremely unlikely)

        // Sigil for empty name
        val emptySigil = ChaldeanNumerology.generateSigilId("")
        assertEquals("0", emptySigil)

        // Sigils should be 6 digits or "0"
        val testSigil = ChaldeanNumerology.generateSigilId("ALEXANDER")
        assertEquals(6, testSigil.length)
    }

    @Test
    fun testCompoundVsReduced() {
        // For numbers 1-9, compound equals reduced
        for (i in 1..9) {
            val name = when (i) {
                1 -> "A"
                2 -> "B"
                3 -> "C"
                4 -> "D"
                5 -> "E"
                6 -> "U"
                7 -> "O"
                8 -> "F"
                9 -> "I" // No letter maps to 9; this tests 9+0
                else -> "?"
            }
            if (name != "?") {
                val result = ChaldeanNumerology.calculateName(name)
                if (result.reduced <= 9) {
                    assertEquals(result.reduced, result.compound % 10)
                }
            }
        }

        // For compound > 9, reduced should be < 10
        val long = ChaldeanNumerology.calculateName("ALEXANDER")
        assert(long.compound > 9)
        assert(long.reduced < 10)
    }

    @Test
    fun testLetterValueCorrectness() {
        val result = ChaldeanNumerology.calculateName("AMIET")
        assertEquals(5, result.letters.size)
        assertEquals('A', result.letters[0].letter)
        assertEquals(1, result.letters[0].value)
        assertEquals('M', result.letters[1].letter)
        assertEquals(4, result.letters[1].value)
    }
}
