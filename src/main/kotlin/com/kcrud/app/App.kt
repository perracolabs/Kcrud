package com.kcrud.app

import com.kcrud.app.plugins.*
import com.kcrud.app.plugins.configureGraphQL
import com.kcrud.app.plugins.configureRouting
import com.kcrud.data.database.DatabaseFactory
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

    DatabaseFactory.init(mode= DatabaseFactory.Mode.PERSISTENT, type = DatabaseFactory.DBType.H2)
}
