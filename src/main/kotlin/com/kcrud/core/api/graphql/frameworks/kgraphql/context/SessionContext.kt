/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.api.graphql.frameworks.kgraphql.context

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.ContextBuilder
import com.kcrud.core.admin.env.security.user.ContextUser
import com.kcrud.core.admin.env.security.user.UserRole
import com.kcrud.core.api.graphql.frameworks.kgraphql.annotation.KGraphQLAPI
import com.kcrud.core.utils.Tracer
import io.ktor.server.application.*

/**
 * Graphql session context hold attributes such as the context user.
 */
@KGraphQLAPI
internal class SessionContext(private val context: Context) {
    private val tracer = Tracer<SessionContext>()

    @Suppress("MemberVisibilityCanBePrivate")
    fun getUser(): ContextUser? {
        return context.get<ContextUser>()
    }

    fun printUser() {
        val user: ContextUser? = getUser()

        user?.let {
            tracer.info("Context user: ${it.id}. Role: ${it.role}.")
        } ?: tracer.info("No context user found.")
    }

    companion object {
        /**
         * This is a very naive simple example just adding a header key-value pair.
         * as an example of how to add a user to the map that will be injected into
         * the GraphQL context.
         * In a real application, this could be a JWT token decoded from the bearer key.
         */
        fun injectUserFromHeader(contextBuilder: ContextBuilder, call: ApplicationCall) {
            // In a real application, the role should ideally
            // be retrieved from a database or another source.
            call.request.headers[ContextUser.KEY_USER]?.let { userId ->
                contextBuilder.inject(
                    ContextUser::class,
                    ContextUser(
                        id = userId,
                        role = UserRole.ADMIN
                    )
                )
            }
        }
    }
}
