/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud

import com.kcrud.data.database.shared.DatabaseManager
import com.kcrud.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

/**
 * Application main entry point.
 *
 * See: [Choosing an engine](https://ktor.io/docs/engines.html)
 */
fun main(args: Array<String>) {
    EngineMain.main(args)
}

/**
 * Application configuration module, responsible for setting up various
 * features like authentication, routing, and database initialization
 * for the Ktor application.
 *
 * Suppressing the 'unused' warning, as the linter is unaware that the
 * module is specified in the 'application.conf' file using HOCON format.
 *
 * See: [Modules](https://ktor.io/docs/modules.html)
 *
 * See: [Plugins](https://ktor.io/docs/plugins.html#install)
 */
@Suppress("unused")
fun Application.module() {

    configureSettingsProvider()

    configureHTTP()

    configureRateLimit()

    configureAuthentication()

    configureStatusPages()

    configureSerialization()

    configureDependencyInjection()

    configureRouting()

    configureGraphQL()

    DatabaseManager.init(
        mode = DatabaseManager.Mode.PERSISTENT,
        type = DatabaseManager.DBType.H2
    )
}
