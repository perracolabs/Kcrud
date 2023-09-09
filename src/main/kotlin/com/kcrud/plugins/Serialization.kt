/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

/**
 * Sets up and installs the ContentNegotiation feature for JSON serialization.
 *
 * See: [Kotlin Serialization Guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md)
 */
fun Application.configureSerialization() {

    install(ContentNegotiation) {
        // Define the behavior and characteristics of the JSON serializer.
        json(Json {
            prettyPrint = true         // Format JSON output for easier reading.
            isLenient = true           // Allow flexible parsing of incoming JSON.
            encodeDefaults = true      // Serialize properties with default values.
            ignoreUnknownKeys = false  // Fail on unknown keys in the incoming JSON.
        })
    }
}



