/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud

import com.kcrud.data.database.shared.DatabaseManager
import com.kcrud.plugins.*
import com.kcrud.settings.SettingsProvider
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

/**
 * Application main entry point.
 *
 * See: [Choosing an engine](https://ktor.io/docs/engines.html)
 */
fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

/**
 * Application configuration module, responsible for setting up various
 * features like authentication, routing, and database initialization
 * for the Ktor application.
 *
 * See: [Modules](https://ktor.io/docs/modules.html)
 *
 * See: [Plugins](https://ktor.io/docs/plugins.html#install)
 */
fun Application.module() {

    settingsProviderModule()

    koinModule()

    httpModule()

    rateLimitModule()

    authenticationModule()

    statusPagesModule()

    routingModule()

    graphQLModule()

    DatabaseManager.init(
        mode = DatabaseManager.Mode.PERSISTENT,
        type = DatabaseManager.DBType.H2
    )

    val logger = LoggerFactory.getLogger(javaClass.simpleName)
    logger.info("Server configured. Development Mode: ${SettingsProvider.global.development}")
}
