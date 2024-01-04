/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.api.graphql.frameworks.expedia.context

import com.kcrud.core.admin.env.security.user.ContextUser
import com.kcrud.core.admin.env.security.user.UserRole
import com.kcrud.core.api.graphql.frameworks.expedia.annotation.ExpediaAPI
import com.kcrud.core.utils.Tracer
import graphql.schema.DataFetchingEnvironment
import io.ktor.http.*

/**
 * Graphql session context hold attributes such as the context user.
 */
@ExpediaAPI
internal class SessionContext(private val env: DataFetchingEnvironment) {
    private val tracer = Tracer<SessionContext>()

    @Suppress("MemberVisibilityCanBePrivate")
    fun getUser(): ContextUser? {
        return env.graphQlContext.getOrDefault(ContextUser.KEY_USER, null)
    }

    fun printUser() {
        val user: ContextUser? = getUser()

        user?.let { contextUser ->
            tracer.info("Context user: ${contextUser.id}. Role: ${contextUser.role}.")
        } ?: tracer.info("No context user found.")
    }

    companion object {
        /**
         * This is a very naive simple example just adding a header key-value pair.
         * as an example of how to add a user to the map that will be injected into
         * the GraphQL context.
         * In a real application, this could be a JWT token decoded from the bearer key.
         */
        fun injectUserFromHeader(map: MutableMap<String, Any>, headers: Headers) {
            // In a real application, the role should ideally
            // be retrieved from a database or another source.
            headers[ContextUser.KEY_USER]?.let { userId ->
                map[ContextUser.KEY_USER] = ContextUser(
                    id = userId,
                    role = UserRole.ADMIN
                )
            }
        }
    }
}
