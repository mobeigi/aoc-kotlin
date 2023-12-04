package day04

import kotlin.math.pow
import readInput

private object Day04 {
    data class Card(val id: Int, val winningNumbers: Set<Int>, val playerNumbers: Set<Int>)

    fun part1(input: List<String>): Int {
        val cards = parseToCards(input)
        return cards.sumOf { card ->
            val numOfWinningNumbers = card.playerNumbers.count { card.winningNumbers.contains(it) }
            doublingSequenceAtN(numOfWinningNumbers)
        }
    }

    fun part2(input: List<String>): Int {
        val cards = parseToCards(input)
        val runningTotalOfCardsTouchedMap = hashMapOf<Int, Int>()

        // Iterate in reverse as we can tally totals for processed cards beneath us as we go up
        cards.reversed().forEach { card ->
            val numOfWinningNumbers = card.playerNumbers.count { card.winningNumbers.contains(it) }
            val runningTotalOfCardsTouched =
                1.plus(
                    (1..numOfWinningNumbers).sumOf { offset ->
                        runningTotalOfCardsTouchedMap[card.id + offset]
                            ?: throw IllegalArgumentException("No solution exists")
                    }
                )
            runningTotalOfCardsTouchedMap[card.id] = runningTotalOfCardsTouched
        }
        return runningTotalOfCardsTouchedMap.values.sum()
    }

    /** Doubling Sequence formula: f(x) = 2^(n-1) */
    private fun doublingSequenceAtN(n: Int) = (2.0).pow(n - 1).toInt()

    private fun parseToCards(input: List<String>): List<Card> =
        input.map { line ->
            val split = line.split(':', '|')
            val id = split[0].split(' ').last().toInt()
            val winningNumbers =
                split[1].trim().split(' ').filter { it.isNotBlank() }.map { it.toInt() }.toSet()
            val playerNumbers =
                split[2].trim().split(' ').filter { it.isNotBlank() }.map { it.toInt() }.toSet()
            Card(id = id, winningNumbers = winningNumbers, playerNumbers = playerNumbers)
        }
}

fun main() {
    val input = readInput(Day04.javaClass.simpleName)

    // Test part 1
    val part1Result = Day04.part1(input)
    println(part1Result)
    check(part1Result == 22193)

    // Test part 2
    val part2Result = Day04.part2(input)
    println(part2Result)
    check(part2Result == 5625994)
}
