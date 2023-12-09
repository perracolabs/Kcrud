/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

/**
 * Sets up and installs the ContentNegotiation feature for JSON serialization.
 *
 * See: [Kotlin Serialization](https://ktor.io/docs/serialization.html)
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        // Define the behavior and characteristics of the JSON serializer.
        jackson {
            // Format JSON output for easier reading.
            enable(SerializationFeature.INDENT_OUTPUT)
            writerWithDefaultPrettyPrinter()

            // Fail on unknown keys in the incoming JSON.
            enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

            // Register the JavaTimeModule to support java.time.* types like LocalDate and LocalDateTime
            registerModule(JavaTimeModule())
        }
    }
}
