package com.kcrud.app.configuration

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*


/**
 * Extension function to configure HTTP settings for a Ktor Application.
 */
fun Application.configureHTTP() {

    // Install the CORS feature to handle Cross-Origin Resource Sharing.
    install(CORS) {

        // Allow specific HTTP methods.
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)

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
