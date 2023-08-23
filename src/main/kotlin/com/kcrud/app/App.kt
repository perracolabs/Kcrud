package com.kcrud.app

import com.kcrud.app.configuration.*
import io.ktor.server.application.*

@Suppress("unused")
fun Application.module() {
    configureHTTP()
    configureDependencyInjection()
    configureSerialization()
    configureRouting()
    DatabaseFactory.init(DatabaseFactory.Mode.PERSISTENT)
}
