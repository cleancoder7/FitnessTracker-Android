package com.flinesoft.fitnesstracker.calculation

import com.flinesoft.fitnesstracker.globals.extensions.durationSince
import com.flinesoft.fitnesstracker.globals.extensions.minusKt
import org.joda.time.DateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

// Source: https://en.wikipedia.org/wiki/Moving_average
@ExperimentalTime
object MovingAverageCalculator {
    data class DataEntry(val measureDate: DateTime, val value: Double)

    /** Calculates the moving average for a given sorted list of time series data. Newer entries are weighted higher. Returns `null` if not enough data. */
    fun calculateMovingAverageAt(
        date: DateTime,
        timeIntervalToConsider: Duration,
        dataEntries: List<DataEntry>,
        minDataEntriesCount: Int,
        maxWeightFactor: Double
    ): Double? {
        val fromDate = date.minusKt(timeIntervalToConsider)

        val filteredDataEntries = filteredDataEntries(
            fromDate = fromDate,
            toDate = date,
            dataEntries = dataEntries,
            minDataEntriesCount = minDataEntriesCount
        ) ?: return null

        var sumOfWeights = 0.0
        var sumOfWeightedValues = 0.0

        for (dataEntry in filteredDataEntries) {
            val weight = weightAt(date = dataEntry.measureDate, fromDate = fromDate, toDate = date, maxWeightFactor = maxWeightFactor)
            sumOfWeights += weight
            sumOfWeightedValues += weight * dataEntry.value
        }

        return sumOfWeightedValues / sumOfWeights
    }

    fun weightAt(date: DateTime, fromDate: DateTime, toDate: DateTime, maxWeightFactor: Double): Double {
        val percentileInDateRange = date.durationSince(fromDate) / toDate.durationSince(fromDate)
        return (maxWeightFactor - 1) * percentileInDateRange + 1
    }

    /** Filters all data entries outside of the interval and makes sure there are projected data entries for the edges of the interval. */
    fun filteredDataEntries(
        fromDate: DateTime,
        toDate: DateTime,
        dataEntries: List<DataEntry>,
        minDataEntriesCount: Int
    ): List<DataEntry>? {
        val firstEntryAfterDate = dataEntries.firstOrNull { it.measureDate.isAfter(toDate) }
        val lastEntryBeforeInterval = dataEntries.lastOrNull { it.measureDate.isBefore(fromDate) }

        val dataEntriesInInterval = dataEntries
            .dropWhile { it.measureDate.isBefore(fromDate) }
            .dropLastWhile { it.measureDate.isAfter(toDate) }
            .toMutableList()

        if (dataEntriesInInterval.size < minDataEntriesCount) return null

        val projectedFirstDataEntry = lastEntryBeforeInterval?.run {
            val firstEntry = dataEntriesInInterval.first()
            DataEntry(
                measureDate = fromDate,
                value = (value * firstEntry.measureDate.durationSince(fromDate).inSeconds + firstEntry.value * fromDate.durationSince(measureDate).inSeconds) /
                            firstEntry.measureDate.durationSince(measureDate).inSeconds
            )
        }
        projectedFirstDataEntry?.let { dataEntriesInInterval.add(0, it) }

        val projectedLastDataEntry = firstEntryAfterDate?.run {
            val lastEntry = dataEntriesInInterval.last()
            DataEntry(
                measureDate = toDate,
                value = (lastEntry.value * measureDate.durationSince(toDate).inSeconds + value * toDate.durationSince(lastEntry.measureDate).inSeconds) /
                            measureDate.durationSince(lastEntry.measureDate).inSeconds
            )
        }
        projectedLastDataEntry?.let { dataEntriesInInterval.add(it) }

        return dataEntriesInInterval
    }
}
