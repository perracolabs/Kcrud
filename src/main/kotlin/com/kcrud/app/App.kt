package com.kcrud.app

import com.kcrud.app.configuration.*
import com.kcrud.app.configuration.configureGraphQL
import io.ktor.server.application.*


/**
 * Main entry point for the Ktor application module.
 */
@Suppress("unused")
fun Application.module() {

    configureHTTP()

    configureDependencyInjection()

    configureSerialization()

    configureRouting()

    configureGraphQL()

    DatabaseFactory.init(mode=DatabaseFactory.Mode.PERSISTENT, type = DatabaseFactory.DBType.H2)
}
