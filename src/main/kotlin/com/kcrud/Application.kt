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
 * See: [Modules](https://ktor.io/docs/modules.html)
 *
 * See: [Plugins](https://ktor.io/docs/plugins.html#install)
 */
fun Application.module() {

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

    val tracer = Tracer.forFunction(Application::module)
    tracer.byDeploymentType("Development Mode Enabled: ${SettingsProvider.server.development}.")
    tracer.info("Server configured. Deployment Type: ${SettingsProvider.deployment.type}.")
}
