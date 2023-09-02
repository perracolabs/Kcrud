package com.kcrud.app.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*


/**
 * Initializes CORS (Cross-Origin Resource Sharing) settings for the application.
 *
 * Configures allowed HTTP methods, headers, and other CORS-specific settings
 * such as permitting credentials and non-simple content types.
 *
 * See: [CORS Documentation](https://ktor.io/docs/cors.html).
 */
fun Application.configureHTTP() {

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

        // Enable inclusion of credentials in CORS requests.
        allowCredentials = true

        // Enable non-simple content types, allowing for more complex operations like file uploads.
        allowNonSimpleContentTypes = true
    }
}

