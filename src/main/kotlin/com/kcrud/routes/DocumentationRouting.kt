/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.settings.SettingsProvider
import com.kcrud.utils.NetworkUtils
import io.ktor.server.http.content.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen

/**
 * Configures Swagger-UI, OpenAPI and Redoc.
 *
 * The server list must be manually updated in the yaml file.
 *
 * WiIth JetBrains Ultimate Edition, the documentation can be auto-generated following the next steps:
 *  1. Place the caret over the 'routing' instruction defined above at the start of this function.
 *  2. Press Alt+Enter, and select 'Generate Swagger/OpenAPI Documentation'.
 *
 * See [Swagger-UI](https://ktor.io/docs/swagger-ui.html#configure-swagger)
 *
 * See [OpenAPI](https://ktor.io/docs/openapi.html)
 *
 * See [Redoc](https://swagger.io/blog/api-development/redoc-openapi-powered-documentation/)
 */
fun Route.documentationRouting() {

    if (SettingsProvider.docs.isEnabled) {
        val yamlFile = SettingsProvider.docs.yamlFile
        val rootPath = SettingsProvider.deployment.apiVersion

        // Root path.
        staticResources(remotePath = rootPath, basePackage = "openapi")

        // Swagger-UI.
        val swaggerPath = SettingsProvider.docs.swaggerPath
        swaggerUI(path = swaggerPath, swaggerFile = yamlFile) { version = "4.15.5" }

        // OpenAPI.
        val openApiPath = SettingsProvider.docs.openApiPath
        openAPI(path = openApiPath, swaggerFile = yamlFile) { codegen = StaticHtmlCodegen() }

        // Redoc.
        val redocPath = "$rootPath/docs${SettingsProvider.docs.redocPath}"

        val endpoints = listOf(redocPath, swaggerPath, openApiPath)
        NetworkUtils.logEndpoints(reason = "Redoc / Swagger-UI / OpenApi", endpoints = endpoints)
    }
}
