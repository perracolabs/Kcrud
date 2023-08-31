package com.kcrud.app.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*


/**
 * Application extension function to configure HTTP settings.
 *
 * See: [CORS Documentation](https://ktor.io/docs/cors.html)
 */
fun Application.configureHTTP() {

    // Install the CORS feature to handle Cross-Origin Resource Sharing.
    install(CORS) {

        // Allow specific HTTP methods.
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)

        // Allow specific headers.
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)

        // Allow requests from any host.
        // Note: This is not recommended for production environments.
        anyHost()

        // Allow credentials like cookies and HTTP authentication to be included in CORS requests.
        allowCredentials = true

        // Allow non-simple content-types, enabling scenarios like file uploads.
        allowNonSimpleContentTypes = true
    }
}
