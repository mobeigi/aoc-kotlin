package day06

import readInput

private object Day06 {
    private const val ACCELERATION_MM_PER_ML = 1.0

    data class Race(val duration: Long, val recordDistance: Long)
    data class RaceOption(val holdButtonDuration: Long)

    fun part1(input: List<String>): Int {
        val races = parseToRaces(input)
        return races
            .map { race -> race.numberOfWaysToBeatRecord() }
            .reduce { acc, i -> acc.times(i) }
    }

    fun part2(input: List<String>): Int {
        val races = parseToRaces(input)
        val duration = races.joinToString(separator = "") { it.duration.toString() }.toLong()
        val recordDistance = races.joinToString(separator = "") { it.recordDistance.toString() }.toLong()
        val singleRace = Race(duration, recordDistance)
        return singleRace.numberOfWaysToBeatRecord()
    }

    private fun parseToRaces(input: List<String>): List<Race> {
        val duration =
            input[0].split(':', ' ').drop(1).filterNot { it.isBlank() }.map { it.trim().toLong() }
        val distance =
            input[1].split(':', ' ').drop(1).filterNot { it.isBlank() }.map { it.trim().toLong() }
        return duration.mapIndexed { index, time ->
            Race(duration = time, recordDistance = distance[index])
        }
    }

    /**
     * Find the total number of ways to beat record.
     * We do this by starting at the very middle of our duration range and working our way
     * from the middle out. This is because it is guaranteed that the middle element(s) will cover
     * the maximum distance and the results are symmetric on the left and right sides of the middle
     * element(s). Therefore, we can short circuit as soon as we find a result that doesn't beat the record.
     */
    fun Race.numberOfWaysToBeatRecord(): Int {
        // For odd durations: l/r will point to the same single middle element
        // For even durations: l/r will point to two adjacent middle elements
        val l = duration / 2
        val r = if (duration.mod(2) == 0) l else (duration / 2) + 1
        val result = mutableListOf<RaceOption>()

        tailrec fun processMiddleOut(duration: Long, l: Long, r: Long) {
            if (l < 0 || r > duration) {
                return
            }

            val raceOptionL = RaceOption(l)
            val raceOptionR = RaceOption(r)

            val leftDistance = computeAchievableDistance(duration, raceOptionL)
            val rightDistance = computeAchievableDistance(duration, raceOptionR)
            check(leftDistance == rightDistance)

            // Only need to check left due to symmetry around middle element(s)
            if (leftDistance <= recordDistance) {
                return
            }

            // Special case only for first iteration of odd durations
            if (l == r) {
                result.add(raceOptionL)
            } else {
                result.add(raceOptionL)
                result.add(raceOptionR)
            }
            processMiddleOut(duration, l - 1, r + 1)
        }

        processMiddleOut(duration, l, r)
        return result.count()
    }

    /**
     * achievable distance is how much distance you can cover depending on how long you hold the button for
     */
    private fun computeAchievableDistance(duration: Long, raceOption: RaceOption): Long =
        ((raceOption.holdButtonDuration * ACCELERATION_MM_PER_ML) * (duration - raceOption.holdButtonDuration)).toLong()
}

fun main() {
    val input = readInput(Day06.javaClass.simpleName)

    // Test part 1
    val part1Result = Day06.part1(input)
    println(part1Result)
    check(part1Result == 800280)

    // Test part 2
    val part2Result = Day06.part2(input)
    println(part2Result)
    check(part2Result == 45128024)
}
