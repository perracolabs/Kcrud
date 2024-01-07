/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.data.utils

import kcrud.base.data.serializers.SUUID
import java.util.*

/**
 * Converts a [String] to a [UUID] or returns null if the string is not a valid UUID.
 *
 * @return The [UUID] representation of the string, or null if the string is not a valid UUID.
 */
fun String?.toUUIDOrNull(): SUUID? {
    return try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

/**
 * Converts a given string representation of a UUID to a UUID object.
 *
 * @return a UUID object converted from the string representation.
 * @throws IllegalArgumentException if the string is not a valid UUID.
 */
fun String?.toUUID(): SUUID {
    return try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("String '$this' is not a valid UUID.")
    }
}
