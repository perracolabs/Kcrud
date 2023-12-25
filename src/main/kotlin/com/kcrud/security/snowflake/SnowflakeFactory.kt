/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.security.snowflake

import com.kcrud.settings.SettingsProvider
import com.kcrud.system.Tracer
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
    private val tracer = Tracer<SnowflakeFactory>()

    // Lazy-loaded machine ID, configured per-machine.
    private val machineId by lazy { SettingsProvider.server.machineId }

    // Tracks the last timestamp in milliseconds when an ID was generated.
    // Initialized to -1 to indicate no IDs have been generated yet.
    private var lastTimestampMs = -1L

    // Tracks the sequence number within the same millisecond.
    // This is used to generate multiple unique IDs within the same millisecond.
    // Initialized to 0, as it gets incremented for each ID generated within the same millisecond.
    // If IDs are not generated at a frequency higher than one per millisecond, this value
    // will typically be 0.
    private var sequence = 0L

    // Number of bits allocated for the machine ID. Determines the maximum number of unique machine IDs.
    private const val MACHINE_ID_BITS = 10

    // Number of bits allocated for the sequence number.
    // Controls the maximum number of IDs that can be generated per millisecond for each machine.
    private const val SEQUENCE_BITS = 12

    // The base used for converting the generated ID to an alphanumeric string.
    // Base 36 combines 0-9 and a-z for a compact representation.
    private const val ALPHA_NUMERIC_BASE = 36

    // Maximum possible value for machine ID, derived from the number of bits allocated.
    // This value is 2^MACHINE_ID_BITS - 1.
    private const val MAX_MACHINE_ID = (1 shl MACHINE_ID_BITS) - 1L

    // Maximum possible value for the sequence number, based on the allocated bits.
    // Equals 2^SEQUENCE_BITS - 1, ensuring a unique ID sequence within a millisecond.
    private const val MAX_SEQUENCE = (1 shl SEQUENCE_BITS) - 1L

    // Wall-clock reference time for Snowflake IDs.
    private val timestampEpoch = System.currentTimeMillis()

    // Starting point for elapsed time measurement with System.nanoTime().
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
            tracer.error("Invalid System Clock. Current timestamp: $currentTimestampMs, last timestamp: $lastTimestampMs")
            throw IllegalStateException("Invalid System Clock.")
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
