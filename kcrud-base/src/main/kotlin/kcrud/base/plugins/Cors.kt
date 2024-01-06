/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import kcrud.base.admin.settings.AppSettings
import kcrud.base.utils.Tracer

/**
 * Configures CORS settings by setting allowed HTTP methods and headers, permitting credentials,
 * and enabling non-simple content types for more complex operations like file uploads.
 *
 * Note: The 'anyHost' setting for CORS is not recommended for production use.
 *
 * See: [CORS Documentation](https://ktor.io/docs/cors.html)
 */
fun Application.configureCors() {

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

        // Enable inclusion of credentials in CORS requests.
        allowCredentials = true

        // Enable non-simple content types,
        // allowing for more complex operations like file uploads.
        allowNonSimpleContentTypes = true

        // Set the allowed hosts.

        val tracer = Tracer.forFunction(funcRef = Application::configureCors)

        val allowedHosts: List<String> = AppSettings.cors.allowedHosts
        tracer.info("Allowed hosts: $allowedHosts")

        if (allowedHosts.isEmpty() or allowedHosts.contains("*")) {
            anyHost()
            tracer.byEnvironment("Allowing all hosts.")
        } else {
            allowedHosts.forEach { host -> allowHost(host) }
        }
    }
}
