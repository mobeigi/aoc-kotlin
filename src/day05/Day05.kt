package day05

import readInput

private object Day05 {
    enum class RangePosition {
        START,
        END
    }

    data class ConversionMapBinarySearchWrapper(
        val rangeValue: Long,
        val rangePosition: RangePosition,
        val conversionMap: ConversionMap
    )
    // Poorly named but from the spec!
    data class ConversionMap(val destination: Long, val source: Long, val rangeLength: Long)

    data class Almanac(
        val seeds: Set<Long>,
        val seedToSoil: List<ConversionMap>,
        val soilToFertilizer: List<ConversionMap>,
        val fertilizerToWater: List<ConversionMap>,
        val waterToLight: List<ConversionMap>,
        val lightToTemperature: List<ConversionMap>,
        val temperatureToHumidity: List<ConversionMap>,
        val humidityToLocation: List<ConversionMap>,
    )

    fun part1(input: List<String>): Long {
        val almanac = parseToAlmanac(input)
        val pipeline = almanac.toRangeBSPipeline()

        return almanac.seeds.minOf { seed ->
            var nextValue = seed
            pipeline.forEach { nextValue = it.process(nextValue) }
            nextValue
        }
    }

    fun part2(input: List<String>): Long {
        val almanac = parseToAlmanac(input)
        val pipeline = almanac.toRangeBSPipeline()

        // This is very slow as we are basically pushing every number through the pipeline
        return almanac.seeds
            .chunked(2)
            .asSequence()
            .flatMap { (rangeStart, rangeOffset) -> rangeStart until rangeStart + rangeOffset }
            .minOf { seed ->
                var nextValue = seed
                pipeline.forEach { nextValue = it.process(nextValue) }
                nextValue
            }
    }

    private fun parseToAlmanac(input: List<String>): Almanac {
        val splitComponents = input.split { it.isBlank() }
        val seeds =
            splitComponents[0]
                .first()
                .split(':')
                .last()
                .trim()
                .split(' ')
                .map { it.toLong() }
                .toSet()
        val seedToSoil = parseConversionMapAtIndex(splitComponents, 1)
        val soilToFertilizer = parseConversionMapAtIndex(splitComponents, 2)
        val fertilizerToWater = parseConversionMapAtIndex(splitComponents, 3)
        val waterToLight = parseConversionMapAtIndex(splitComponents, 4)
        val lightToTemperature = parseConversionMapAtIndex(splitComponents, 5)
        val temperatureToHumidity = parseConversionMapAtIndex(splitComponents, 6)
        val humidityToLocation = parseConversionMapAtIndex(splitComponents, 7)
        return Almanac(
            seeds,
            seedToSoil,
            soilToFertilizer,
            fertilizerToWater,
            waterToLight,
            lightToTemperature,
            temperatureToHumidity,
            humidityToLocation
        )
    }

    private fun parseConversionMapAtIndex(
        splitComponents: List<List<String>>,
        index: Int
    ): List<ConversionMap> {
        return splitComponents[index].drop(1).map {
            val (destination, source, rangeLength) = it.split(' ')
            ConversionMap(
                destination = destination.toLong(),
                source = source.toLong(),
                rangeLength = rangeLength.toLong()
            )
        }
    }

    private fun <T> List<T>.split(predicate: (T) -> Boolean): List<List<T>> {
        val index = this.indexOfFirst(predicate)
        return if (index == -1) {
            listOf(this)
        } else {
            return listOf(this.take(index)) + this.drop(index + 1).split(predicate)
        }
    }

    private fun List<ConversionMap>.toRangeBSList(): List<ConversionMapBinarySearchWrapper> {
        val list = mutableListOf<ConversionMapBinarySearchWrapper>()
        this.forEach {
            list.add(ConversionMapBinarySearchWrapper(it.source, RangePosition.START, it))
            list.add(ConversionMapBinarySearchWrapper(it.source + it.rangeLength - 1, RangePosition.END, it))
        }
        list.sortBy { it.rangeValue }
        return list
    }

    /**
     * Create ordered pipeline to process numbers through.
     */
    private fun Almanac.toRangeBSPipeline(): List<List<ConversionMapBinarySearchWrapper>> =
        listOf(
            this.seedToSoil.toRangeBSList(),
            this.soilToFertilizer.toRangeBSList(),
            this.fertilizerToWater.toRangeBSList(),
            this.waterToLight.toRangeBSList(),
            this.lightToTemperature.toRangeBSList(),
            this.temperatureToHumidity.toRangeBSList(),
            this.humidityToLocation.toRangeBSList(),
        )

    /**
     * Process conversion map using binary search.
     * This is results in quick resolutions for a single input.
     */
    private fun List<ConversionMapBinarySearchWrapper>.process(input: Long): Long {
        val targetIndex =
            binarySearchBy(input) { it.rangeValue }
                .let {
                    // No exact match found
                    if (it < 0) {
                        // Set to ideal insertion point
                        return@let -(it + 1)
                    }
                    it
                }

        if (targetIndex < this.size) {
            var entry = this[targetIndex]
            if (entry.rangePosition == RangePosition.END) {
                entry = this[targetIndex - 1]
            }
            if (
                input >= entry.conversionMap.source &&
                    input < entry.conversionMap.source + entry.conversionMap.rangeLength
            ) {
                // Within range
                return entry.conversionMap.destination + (input - entry.conversionMap.source)
            }
        }
        return input
    }
}

fun main() {
    val input = readInput(Day05.javaClass.simpleName)

    // Test part 1
    val part1Result = Day05.part1(input)
    println(part1Result)
    check(part1Result == 484023871L)

    // Test part 2
    val part2Result = Day05.part2(input)
    println(part2Result)
    check(part2Result == 46294175L)
}
