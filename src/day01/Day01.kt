package day01

import day01.Day01.part1
import day01.Day01.part2
import println
import readInput

object Day01 {
    private val digitCharArray = ('0'..'9').toList().toCharArray()
    private val spelledDigits = listOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
    )
    fun part1(input: List<String>): Int =
        input.sumOf {line ->
            val digits = getLeftAndRightDigits(line)
            check(digits.first != null && digits.second != null)
            "${digits.first!!.value}${digits.second!!.value}".toInt()
        }

    fun part2(input: List<String>): Int =
        input.sumOf { line ->
            // Process normal digits
            val digits = getLeftAndRightDigits(line)

            // Process spelled digits
            val spelledDigits = getLeftAndRightSpelledDigits(line)

            check(digits.first != null || spelledDigits.first != null)
            check(digits.second != null || spelledDigits.second != null)

            val leftDigit = if (digits.first == null) {
                spelledDigits.first!!.value
            }
            else if (spelledDigits.first == null) {
                digits.first!!.value
            }
            else {
                if (digits.first!!.index < spelledDigits.first!!.index) {
                    digits.first!!.value
                }
                else {
                    spelledDigits.first!!.value
                }
            }

            val rightDigit = if (digits.second == null) {
                spelledDigits.second!!.value
            }
            else if (spelledDigits.second == null) {
                digits.second!!.value
            }
            else {
                if (digits.second!!.index > spelledDigits.second!!.index) {
                    digits.second!!.value
                }
                else {
                    spelledDigits.second!!.value
                }
            }

            "${leftDigit}${rightDigit}".toInt()
        }

    private fun getLeftAndRightDigits(line: String): Pair<IndexedValue<Int>?, IndexedValue<Int>?> {
        var leftDigitIndex = line.indexOfAny(digitCharArray)
        var rightDigitIndex = line.lastIndexOfAny(digitCharArray)

        if (leftDigitIndex == -1 && rightDigitIndex == -1) {
            return Pair(null, null)
        }

        // Handle single digit case
        if (leftDigitIndex == -1) {
            leftDigitIndex = rightDigitIndex
        }
        else if (rightDigitIndex == -1) {
            rightDigitIndex = leftDigitIndex
        }

        val leftDigit = line[leftDigitIndex]
        val rightDigit = line[rightDigitIndex]

        return IndexedValue(leftDigitIndex, leftDigit.digitToInt()) to
                IndexedValue(rightDigitIndex, rightDigit.digitToInt())
    }

    private fun getLeftAndRightSpelledDigits(line: String): Pair<IndexedValue<Int>?, IndexedValue<Int>?> {
        var spelledDigitFirstIndex: IndexedValue<Int>? = null
        var spelledDigitLastIndex: IndexedValue<Int>? = null

        spelledDigits.forEach {
            val spelledDigit = it.first
            val index = line.indexOf(spelledDigit)
            val lastIndex = line.lastIndexOf(spelledDigit)

            if (index != -1 && (spelledDigitFirstIndex == null || index < spelledDigitFirstIndex!!.index)) {
                spelledDigitFirstIndex = IndexedValue(index, it.second)
            }
            if (lastIndex != -1 && (spelledDigitLastIndex == null || lastIndex > spelledDigitLastIndex!!.index)) {
                spelledDigitLastIndex = IndexedValue(lastIndex, it.second)
            }
        }
        return spelledDigitFirstIndex to spelledDigitLastIndex
    }
}
fun main() {
    val input = readInput(Day01.javaClass.simpleName)

    // Test part 1
    val part1Result = part1(input)
    part1Result.println()
    check(part1Result == 55029)

    // Test part 2
    val part2Result = part2(input)
    part2Result.println()
    check(part2Result == 55686)
}
