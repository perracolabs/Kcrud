/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.settings.SettingsProvider
import com.kcrud.system.Tracer
import com.kcrud.system.Tracer.Companion.nameWithClass
import com.kcrud.utils.DeploymentType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*

/**
 * Configures HTTP settings for the application, including CORS and default headers.
 *
 * This function sets up CORS by configuring allowed HTTP methods and headers, permitting credentials,
 * and enabling non-simple content types for more complex operations like file uploads.
 *
 * Additionally, it sets a default header 'X-Engine' for all HTTP responses.
 *
 * Note: The 'anyHost' setting for CORS is not recommended for production use.
 *
 * See: [CORS Documentation](https://ktor.io/docs/cors.html)
 *
 * See: [Default Headers Documentation](https://ktor.io/docs/default-headers.html)
 */
fun Application.configureHttpSettings() {


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

        // Enable inclusion of credentials in CORS requests.
        allowCredentials = true

        // Enable non-simple content types,
        // allowing for more complex operations like file uploads.
        allowNonSimpleContentTypes = true

        // Set the allowed hosts.

        val allowedHosts: List<String> = SettingsProvider.cors.allowedHosts
        val tag = Application::configureHttpSettings.nameWithClass<Application>()
        Tracer.byTag(tag = tag).info("Allowed hosts: $allowedHosts")

        if (allowedHosts.isEmpty() or allowedHosts.contains("*")) {
            anyHost()

            when (val deploymentType = SettingsProvider.deployment.type) {
                DeploymentType.PROD -> Tracer.byTag(tag = tag).error("Allowing all hosts in $deploymentType environment.")
                DeploymentType.TEST -> Tracer.byTag(tag = tag).warning("Allowing all hosts in $deploymentType environment.")
                DeploymentType.DEV -> Tracer.byTag(tag = tag).info("Allowing all hosts in $deploymentType environment.")
            }
        } else {
            allowedHosts.forEach { host -> allowHost(host) }
        }
    }
}

