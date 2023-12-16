/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.settings.SettingsProvider
import com.kcrud.utils.NetworkUtils
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen

/**
 * Configures Swagger-UI and OpenAPI.
 *
 * WiIth JetBrains Ultimate Edition, the documentation can be auto-generated following the next steps:
 *  1. Place the caret over the 'routing' instruction defined above at the start of this function.
 *  2. Press Alt+Enter, and select 'Generate Swagger/OpenAPI Documentation'.
 *
 * See [Swagger-UI](https://ktor.io/docs/swagger-ui.html#configure-swagger)
 *
 * See [OpenAPI](https://ktor.io/docs/openapi.html)
 */
fun Route.documentationRouting() {

    SettingsProvider.deployment.swaggerPath?.let { path ->
        if (path.isNotBlank()) {
            swaggerUI(path = path, swaggerFile = "openapi/documentation.yaml") { version = "4.15.5" }
            NetworkUtils.logEndpoint(reason = "Swagger-UI", endpoint = path)
        }
    }

    SettingsProvider.deployment.openApiPath?.let { path ->
        if (path.isNotBlank()) {
            openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml") { codegen = StaticHtmlCodegen() }
            NetworkUtils.logEndpoint(reason = "OpenApi", endpoint = path)
        }
    }
}
