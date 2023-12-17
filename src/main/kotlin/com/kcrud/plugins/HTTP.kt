/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*

/**
 * Initializes CORS (Cross-Origin Resource Sharing) settings for the application.
 *
 * Configures allowed HTTP methods, headers, and other CORS-specific settings
 * such as permitting credentials and non-simple content types.
 *
 * See: [CORS Documentation](https://ktor.io/docs/cors.html)
 */
fun Application.configureHttp() {

    install(DefaultHeaders) {
        header("X-Engine", "Kcrud")
    }

    // Install and configure the CORS feature.
    install(CORS) {

        // Specify allowed HTTP methods for CORS requests.
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)

        // Specify allowed HTTP headers for CORS requests.
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)

        // Allow requests from any host. Not recommended for production.
        anyHost()
        // Host production examples:
        //
        // Allow requests from both http and https, so "http://example.com" and "https://example.com".
        // allowHost(host="example.com")
        //
        // Allow requests from "http://example.com:8081" and "https://example.com:8081".
        // allowHost(host="example.com:8081")
        //
        // Allow requests from "http://api.example.com" and "https://api.example.com".
        // allowHost(host="example.com", subDomains = listOf("api"))
        //
        // Allows requests from "http://example.com" and "https://example.com" specifically,
        // though this is redundant with the first allowHost invocation in this example.
        // allowHost(host="example.com", schemes = listOf("http", "https"))

        // Enable inclusion of credentials in CORS requests.
        allowCredentials = true

        // Enable non-simple content types, allowing for more complex operations like file uploads.
        allowNonSimpleContentTypes = true
    }
}

