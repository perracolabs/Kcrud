package com.kcrud.app

import com.kcrud.app.plugins.*
import com.kcrud.data.database.DatabaseFactory
import io.ktor.server.application.*


/**
 * Ktor Configuration Module.
 *
 * Responsible for setting up various features like authentication,
 * routing, and database initialization for the Ktor application.
 *
 * See: [Plugins](https://ktor.io/docs/plugins.html#install)
 */
@Suppress("unused")
fun Application.module() {

    configureHTTP()

    configureAuthentication()

    configureStatusPages()

    configureTokenGenerator()

    configureSerialization()

    configureDependencyInjection()

    configureRouting()

    configureGraphQL()

    DatabaseFactory.init(mode = DatabaseFactory.Mode.PERSISTENT, type = DatabaseFactory.DBType.H2)
}
