/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.kgraphql.context

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.ContextBuilder
import com.kcrud.graphql.context.ContextUser
import com.kcrud.utils.Tracer
import io.ktor.server.application.*


/**
 * Graphql session context hold attributes such as the context user.
 */
internal class SessionContext(private val context: Context) {
    private val tracer = Tracer<SessionContext>()

    @Suppress("MemberVisibilityCanBePrivate")
    fun getUser(): ContextUser? {
        return context.get<ContextUser>()
    }

    fun printUser() {
        val user: ContextUser? = getUser()

        user?.let {
            tracer.info("Context user: ${it.username}.")
        } ?: tracer.info("No context user found.")
    }

    companion object {
        private const val KEY_USER = "user"

        /**
         * This is a very naive simple example just adding a header key-value pair.
         * as an example of how to add a user to the map that will be injected into
         * the GraphQL context.
         * In a real application, this could be a JWT token decoded from the bearer key.
         */
        fun injectUserFromHeader(contextBuilder: ContextBuilder, call: ApplicationCall) {
            val user = ContextUser(userId = call.request.headers[KEY_USER])
            contextBuilder.inject(ContextUser::class, user)
        }
    }
}
