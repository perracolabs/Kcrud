package com.kcrud.controllers

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Route.user() {

    val controller: UserController by inject()

    route("v1/user/{id}") {
        get { controller.get(call) }

        put { controller.update(call) }

        patch { controller.patch(call) }

        delete { controller.delete(call) }
    }

    route("v1/users") {
        get { controller.getAll(call) }

        delete { controller.deleteAll(call) }
    }

    route("v1/user") {
        post { controller.create(call) }
    }
}
