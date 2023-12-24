/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud

import com.kcrud.data.database.shared.DatabaseManager
import com.kcrud.plugins.*
import com.kcrud.settings.SettingsProvider
import com.kcrud.system.Tracer
import com.kcrud.system.Tracer.Companion.nameWithClass
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

/**
 * Application main entry point.
 *
 * See: [Choosing an engine](https://ktor.io/docs/engines.html)
 */
fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

/**
 * Application configuration module, responsible for setting up the server.
 *
 * The first step is always to load the application settings,
 * so that they can be accessed at any point in the pipeline.
 *
 * The second step is to configure all the plugins and modules
 * ike authentication, routing, CORS, GraphQL, etc.
 *
 * The final step is to initialize the database connection.
 *
 * See: [Modules](https://ktor.io/docs/modules.html)
 *
 * See: [Plugins](https://ktor.io/docs/plugins.html#install)
 */
fun Application.module() {

    // The settings provider must be the first step in the pipeline,
    // so that the plugins can access the configuration settings.
    SettingsProvider.configure(context = this)

    configureKoin()

    configureHttpSettings()

    configureCallLogging()

    configureRateLimit()

    configureAuthentication()

    configureStatusPages()

    configureRoutes()

    configureGraphQL()

    // The database is started last once all
    // the plugins and modules are configured.
    DatabaseManager.init()

    val tag = ::module.nameWithClass<Application>()
    Tracer.byTagAndDeploymentType(tag = tag, message = "Development Mode Enabled: ${SettingsProvider.global.development}.")
    Tracer.byTag(tag = tag).info("Server configured. Deployment Type: ${SettingsProvider.deployment.type}.")
}
