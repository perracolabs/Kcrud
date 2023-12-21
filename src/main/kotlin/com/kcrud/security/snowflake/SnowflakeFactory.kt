/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.security.snowflake

import com.kcrud.settings.SettingsProvider
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.atomic.AtomicInteger

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

    // The maximum allowed machine ID.
    // This constraint ensures that machine IDs are within a safe range.
    // To be considered when defining the machine ID in the configuration.
    private const val MAX_MACHINE_ID = 128

    // The epoch timestamp, used as a base to calculate the unique timestamp part of the ID.
    private const val EPOCH = 1420045200000L

    // Base for converting the numeric ID to an alphanumeric string.
    private const val ALPHA_NUMERIC_BASE = 36

    // Bit shifts for timestamp and machine ID to create a unique ID.
    private const val TIME_STAMP_SHIFT = 22
    private const val MACHINE_ID_SHIFT = 16

    // Maximum value for the increment part of the ID.
    // 16384 (2^14) is chosen to allocate 14 bits for the increment part,
    // allowing for 16384 unique IDs per millisecond per machine.
    private const val MAX_INCREMENT = 16384

    // Atomic counter to ensure uniqueness for IDs generated in the same millisecond.
    private val atomicIncrement = AtomicInteger(0)

    // Lazy-loaded machine ID, configured per-machine.
    private val machineId by lazy { SettingsProvider.global.machineId }

    init {
        require(machineId in 0..<MAX_MACHINE_ID) {
            "Machine ID must between 0 and ${MAX_MACHINE_ID - 1}. Got: $machineId."
        }
    }

    /**
     * Generates the next unique ID as a String.
     *
     * @return The generated unique alphanumeric ID.
     */
    fun nextId(): String {
        synchronized(this) {
            val currentTimestamp: Long = System.currentTimeMillis()
            val timestamp: Long = currentTimestamp - EPOCH

            // The maximum value for the increment part is set to MAX_INCREMENT - 2 to provide a safety margin.
            // This margin helps prevent the atomic increment value from reaching its upper limit, which
            // could potentially cause an overflow or interfere with reserved values at the high end of the range.
            val safeIncrementThreshold: Int = MAX_INCREMENT - 2

            // Resetting the atomic increment to 0 if it reaches or exceeds the maximum limit.
            // This ensures that the increment part of the ID stays within the safe, predefined range
            // and prevents the possibility of generating duplicate IDs or encountering an overflow.
            if (atomicIncrement.get() >= safeIncrementThreshold) {
                atomicIncrement.set(0)
            }

            // Incrementing the atomic counter to get the next unique value for the increment part of the ID.
            val increment = atomicIncrement.incrementAndGet()

            // Constructing the numeric ID by combining the timestamp, machine ID, and increment.
            // These components are shifted and combined in a way to ensure that each part contributes
            // to the uniqueness of the overall ID.
            val numericId = (timestamp shl TIME_STAMP_SHIFT) or (machineId shl MACHINE_ID_SHIFT).toLong() or increment.toLong()

            // Converting the numeric ID to an alphanumeric string for easier use and storage.
            return java.lang.Long.toString(numericId, ALPHA_NUMERIC_BASE)
        }
    }

    /**
     * Parses a unique ID back into its constituent parts.
     *
     * @param id The unique ID to parse.
     * @return A [SnowflakeId] object containing the parsed segments.
     */
    @Suppress("unused")
    fun parse(id: String): SnowflakeId {
        // Parsing the alphanumeric ID back to a numeric value for processing.
        val numericId: Long = java.lang.Long.parseLong(id.lowercase(), ALPHA_NUMERIC_BASE)

        // Extracting the timestamp part from the ID. The original timestamp is retrieved by
        // shifting right by the number of bits allocated for the machine ID and increment,
        // and then adding the epoch offset to get the actual timestamp.
        val timestampMs: Long = (numericId shr TIME_STAMP_SHIFT) + EPOCH
        val timestamp = Instant.fromEpochMilliseconds(timestampMs).toLocalDateTime(timeZone = TimeZone.UTC)

        // Calculating the mask to extract the machine ID and increment value.
        // MAX_MACHINE_ID - 1 is used to create a bitmask that matches the bit length of the machine ID.
        val lowerBitsMask: Long = MAX_MACHINE_ID - 1L

        // Extracting the machine ID part from the ID. This is achieved by shifting right
        // to remove the increment, and then applying a bitmask to isolate the machine ID bits.
        val machineId: Long = (numericId shr MACHINE_ID_SHIFT) and lowerBitsMask

        // Extracting the increment part from the ID. The bitmask isolates the increment bits from the ID.
        val increment: Long = numericId and lowerBitsMask

        return SnowflakeId(
            timestamp = timestamp,
            machineId = machineId.toInt(),
            increment = increment.toInt()
        )
    }
}
