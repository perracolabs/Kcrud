/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.security.snowflake

import com.kcrud.settings.AppSettings
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Generates unique identifiers suitable for distributed systems based
 * on the current system time, machine ID, and an atomic increment value.
 *
 * These IDs are useful in scenarios where unique identification across
 * multiple machines is needed.
 *
 * See: [Snowflake ID](https://en.wikipedia.org/wiki/Snowflake_ID)
 */
internal object SnowflakeFactory {

    // Lazy-loaded machine ID, configured per-machine.
    private val machineId by lazy { AppSettings.server.machineId }

    // Tracks the last timestamp in milliseconds when an ID was generated.
    // Initialized to -1 to indicate no IDs have been generated yet.
    private var lastTimestampMs = -1L

    // Tracks the sequence number within the same millisecond.
    // This is used to generate multiple unique IDs within the same millisecond.
    // Initialized to 0, as it gets incremented for each ID generated within the same millisecond.
    // If IDs are not generated at a frequency higher than one per millisecond, this value
    // will typically be 0.
    private var sequence = 0L

    // Number of bits allocated for the machine ID.
    // 10 bits allows for 2^10 = 1,024 unique machine IDs, supporting large distributed systems.
    private const val MACHINE_ID_BITS = 10

    // Number of bits allocated for the sequence number.
    // 12 bits supports generating up to 2^12 = 4,096 unique IDs per millisecond per machine,
    // catering to high-frequency ID generation requirements.
    private const val SEQUENCE_BITS = 12

    // The base used for converting the generated ID to an alphanumeric string.
    // Base 36 uses digits 0-9 and letters a-z for representation, allowing a compact yet readable format.
    // For example, a numeric value of 12345 in Base 36 might be represented as '9ix' in alphanumeric form.
    private const val ALPHA_NUMERIC_BASE = 36

    // Maximum possible value for machine ID, derived from the number of bits allocated.
    // This value is 2^MACHINE_ID_BITS - 1.
    private const val MAX_MACHINE_ID = (1 shl MACHINE_ID_BITS) - 1L

    // Maximum possible value for the sequence number, based on the allocated bits.
    // Equals 2^SEQUENCE_BITS - 1, ensuring a unique ID sequence within a millisecond.
    private const val MAX_SEQUENCE = (1 shl SEQUENCE_BITS) - 1L

    // Wall-clock reference time captured at the initialization of SnowflakeFactory.
    // This timestamp is used as a reference point in `newTimestamp()` to calculate a robust
    // current time in milliseconds. It provides a stable base time that, when combined with
    // the elapsed time since the factory's initialization (measured using `System.nanoTime()`),
    // creates a timestamp that is immune to system clock adjustments.
    private val timestampEpoch = System.currentTimeMillis()

    // Initial nanosecond-precision timestamp marking the SnowflakeFactory's startup moment.
    // This timestamp is used in conjunction with System.currentTimeMillis() to calculate a robust,
    // monotonic current time in `newTimestamp()`. It ensures that the generated timestamps are always
    // increasing, even in the event of system clock adjustments, by measuring the elapsed time since
    // the factory initialization in a way that's immune to system time changes.
    private val nanoTimeStart = System.nanoTime()

    init {
        // Ensures that the machine ID is within the allowable range.
        require(machineId in 0..MAX_MACHINE_ID) { "The Machine ID must be between 0 and $MAX_MACHINE_ID" }
    }

    /**
     * Generates the next unique Snowflake ID.
     * @return The generated Snowflake ID as a base-36 alphanumeric string.
     * @throws IllegalStateException If the system clock has moved backwards, breaking the ID sequence.
     */
    @Synchronized
    fun nextId(): String {
        var currentTimestampMs = newTimestamp()

        // Check for invalid system clock settings.
        if (currentTimestampMs < lastTimestampMs) {
            throw IllegalStateException(
                "Invalid System Clock. Current timestamp: $currentTimestampMs, last timestamp: $lastTimestampMs"
            )
        }

        // If it's a new millisecond, reset the sequence number.
        if (currentTimestampMs != lastTimestampMs) {
            sequence = 0L
            lastTimestampMs = currentTimestampMs
        } else {
            // If the current timestamp is the same, increment the sequence number.
            // If sequence overflows, wait for the next millisecond.
            if (++sequence > MAX_SEQUENCE) {
                sequence = 0L
                do {
                    Thread.yield()
                    currentTimestampMs = newTimestamp()
                } while (currentTimestampMs <= lastTimestampMs)
                lastTimestampMs = currentTimestampMs
            }
        }

        // Construct the ID.
        val id = (lastTimestampMs shl (MACHINE_ID_BITS + SEQUENCE_BITS)) or
                (machineId.toLong() shl SEQUENCE_BITS) or
                sequence

        return id.toString(radix = ALPHA_NUMERIC_BASE)
    }

    /**
     * Parses a Snowflake ID to extract its segments.
     * The ID is expected to have an optional "id-" prefix.
     * @param id The Snowflake ID to parse.
     * @return SnowflakeData containing the ID segments.
     */
    fun parse(id: String): SnowflakeData {
        val normalizedId = id.toLong(radix = ALPHA_NUMERIC_BASE)

        // Extract the machine ID segment.
        val machineIdSegment = (normalizedId ushr SEQUENCE_BITS) and MAX_MACHINE_ID

        // Extract the timestamp segment.
        val timestampMs = (normalizedId ushr (MACHINE_ID_BITS + SEQUENCE_BITS))
        val instant = Instant.fromEpochMilliseconds(timestampMs)
        val utcTimestampSegment = instant.toLocalDateTime(TimeZone.UTC)

        // Convert the timestamp to LocalDateTime using the system's default timezone.
        val localTimestampSegment = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        // Extract the sequence number segment.
        val sequenceSegment = normalizedId and MAX_SEQUENCE

        return SnowflakeData(
            machineId = machineIdSegment.toInt(),
            sequence = sequenceSegment,
            utc = utcTimestampSegment,
            local = localTimestampSegment
        )
    }

    /**
     * Returns a more robust current timestamp in milliseconds.
     * This method combines `System.currentTimeMillis()` and `System.nanoTime()`
     * to mitigate the impact of system clock adjustments.
     * `System.nanoTime()` is used for its monotonic properties, ensuring the measured
     * elapsed time does not decrease even if the system clock is adjusted.
     * The initial system time (`timestampEpoch`) captured at application startup
     * is combined with the elapsed time since then, calculated using `System.nanoTime()`,
     * to produce a stable and increasing timestamp.
     */
    private fun newTimestamp(): Long {
        val nanoTimeDiff = System.nanoTime() - nanoTimeStart
        return timestampEpoch + TimeUnit.NANOSECONDS.toMillis(nanoTimeDiff)
    }
}
