package com.kcrud.app.configuration

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json


/**
 * Application extension function to configure serialization.
 */
fun Application.configureSerialization() {

    // Install the ContentNegotiation feature to handle serialization.
    install(ContentNegotiation) {

        // Use the json serializer with specific configurations.
        json(Json {
            // Pretty-print JSON output for better readability.
            prettyPrint = true

            // Allow less strict parsing of JSON input, accepting JSON that might not be perfectly formatted.
            isLenient = true

            // Include default values during serialization.
            encodeDefaults = true

            // Do not ignore unknown keys during deserialization.
            ignoreUnknownKeys = false
        })
    }
}

