package day02

import day02.Day02.part1
import day02.Day02.part2
import readInput

private object Day02 {
    data class RevealSet(val red: Int = 0, val green: Int = 0, val blue: Int = 0)

    data class Game(val id: Int, val revealSet: List<RevealSet>)

    fun part1(input: List<String>): Int {
        val games = parseToGames(input)
        val part1MaxCapacity = RevealSet(red = 12, green = 13, blue = 14)
        return games.sumOf { game ->
            val isValidGame =
                game.revealSet.all {
                    val redLeft = part1MaxCapacity.red - it.red
                    val greenLeft = part1MaxCapacity.green - it.green
                    val blueLeft = part1MaxCapacity.blue - it.blue
                    redLeft >= 0 && greenLeft >= 0 && blueLeft >= 0
                }

            if (isValidGame) game.id else 0
        }
    }

    fun part2(input: List<String>): Int {
        val games = parseToGames(input)
        return games.sumOf { game ->
            var minRed = 0
            var minGreen = 0
            var minBlue = 0
            game.revealSet.forEach {
                if (it.red > minRed) {
                    minRed = it.red
                }
                if (it.green > minGreen) {
                    minGreen = it.green
                }
                if (it.blue > minBlue) {
                    minBlue = it.blue
                }
            }

            // compute power of the minimum set of cubes
            minRed * minGreen * minBlue
        }
    }

    /** Parse input into list of Game */
    private fun parseToGames(input: List<String>): List<Game> =
        input.map { line ->
            val components = line.split(":", ";")
            check(components.size > 2) // at least game identifier and 1 reveal set
            val id = components.first().split(" ").last().toInt()
            val revealSet =
                components.drop(1).map {
                    val revealSetComponents = it.split(",")
                    var red = 0
                    var green = 0
                    var blue = 0
                    revealSetComponents.forEach {
                        val (cubeCount, cubeColour) = it.trim().split(" ")
                        when (cubeColour) {
                            "red" -> red = cubeCount.toInt()
                            "green" -> green = cubeCount.toInt()
                            "blue" -> blue = cubeCount.toInt()
                            else -> throw IllegalArgumentException("Unknown colour $cubeColour")
                        }
                    }
                    RevealSet(red = red, green = green, blue = blue)
                }
            Game(id, revealSet)
        }
}

fun main() {
    val input = readInput(Day02.javaClass.simpleName)

    // Test part 1
    val part1Result = part1(input)
    println(part1Result)
    check(part1Result == 2101)

    // Test part 2
    val part2Result = part2(input)
    println(part2Result)
    check(part2Result == 58269)
}
