/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.data.serializers

import kcrud.base.admin.env.security.utils.SecurityUtils
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for Email strings.
 */
object EmailStringSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "EmailString",
        kind = PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: String) {
        if (!SecurityUtils.isValidEmail(email = value)) {
            throw SerializationException("Invalid email format: $value")
        }
        encoder.encodeString(value = value)
    }

    override fun deserialize(decoder: Decoder): String {
        val string: String = decoder.decodeString()
        if (!SecurityUtils.isValidEmail(email = string)) {
            throw SerializationException("Invalid email format: $string")
        }
        return string
    }
}

/**
 * Represents a serializable Email String.
 *
 * @property EmailString The type representing the serializable Email.
 * @see EmailStringSerializer
 */
typealias EmailString = @Serializable(with = EmailStringSerializer::class) String
