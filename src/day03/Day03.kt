package day03

import day03.Day03.part1
import day03.Day03.part2
import java.awt.Point
import readInput

private object Day03 {
    val FLOOD_OFFSET_BOUNDS = setOf(-1, 0, 1)

    fun part1(input: List<String>): Int {
        val array = convertToArray(input)
        val partNumbers = mutableListOf<Int>()

        array.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, item ->
                if (item.isSymbol()) {
                    val point = Point(rowIndex, colIndex)
                    val discoveredPartNumbers = floodSearch(array, point)
                    partNumbers.addAll(discoveredPartNumbers)
                }
            }
        }
        return partNumbers.sum()
    }

    fun part2(input: List<String>): Int {
        val array = convertToArray(input)
        val partNumbers = mutableListOf<Int>()

        array.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, item ->
                if (item.isGearSymbol()) {
                    val point = Point(rowIndex, colIndex)
                    val discoveredPartNumbers = floodSearch(array, point)

                    // Only include ratio if exactly two part numbers found
                    if (discoveredPartNumbers.size == 2) {
                        val gearRatioSum = discoveredPartNumbers.reduce(Int::times)
                        partNumbers.addAll(setOf(gearRatioSum))
                    }
                }
            }
        }
        return partNumbers.sum()
    }

    private fun convertToArray(input: List<String>): Array<CharArray> {
        return input.map { line -> line.toCharArray() }.toTypedArray()
    }

    private fun Char.isSymbol() = !this.isDigit() && this != '.'

    private fun Char.isGearSymbol() = this == '*'

    /**
     * Finds all part numbers around the provided point. It does this by searching the (+1,-1)
     * bounds around the point.
     */
    private fun floodSearch(array: Array<CharArray>, point: Point): Set<Int> {
        val adjacentPartNumbers = mutableSetOf<Int>() // unique results per row
        FLOOD_OFFSET_BOUNDS.forEach { xOffset ->
            FLOOD_OFFSET_BOUNDS.forEach { yOffset ->
                val searchPoint = Point(point.x + xOffset, point.y + yOffset)
                findPartNumberAtPoint(array, searchPoint)?.let { adjacentPartNumbers.add(it) }
            }
        }
        return adjacentPartNumbers
    }

    /**
     * Given a point, determines if value at that point is part of a part number. If so, returns the
     * part number and otherwise returns null.
     */
    private fun findPartNumberAtPoint(array: Array<CharArray>, point: Point): Int? {
        val charAtPoint = array.getOrNull(point)
        if (charAtPoint == null || !charAtPoint.isDigit()) {
            return null
        }

        // First non digit found or the start of the array
        val firstNonDigitYIndex =
            (point.y downTo 0).find { !array[point.x][it].isDigit() }?.plus(1) ?: 0

        val partNumberString = buildString {
            // Search remainder of row for digits to build this part number string
            (firstNonDigitYIndex until array[point.x].size).forEach {
                if (array[point.x][it].isDigit()) {
                    this.append(array[point.x][it])
                } else {
                    return@buildString
                }
            }
        }

        return partNumberString.toInt()
    }

    private fun Array<CharArray>.getOrNull(point: Point): Char? =
        this.getOrNull(point.x)?.getOrNull(point.y)
}

fun main() {
    val input = readInput(Day03.javaClass.simpleName)

    // Test part 1
    val part1Result = part1(input)
    println(part1Result)
    check(part1Result == 544664)

    // Test part 2
    val part2Result = part2(input)
    println(part2Result)
    check(part2Result == 84495585)
}
