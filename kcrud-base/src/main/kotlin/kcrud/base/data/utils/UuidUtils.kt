/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.data.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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

/**
 * Serializer for UUID objects.
 */
internal object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())
}

/**
 * Represents a serializable UUID.
 *
 * @property SUUID The type representing the serializable UUID.
 * @see UUID
 * @see UUIDSerializer
 */
typealias SUUID = @Serializable(with = UUIDSerializer::class) UUID
