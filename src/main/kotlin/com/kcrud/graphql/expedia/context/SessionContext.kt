/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia.context

import com.kcrud.graphql.context.ContextUser
import com.kcrud.utils.Tracer
import graphql.schema.DataFetchingEnvironment
import io.ktor.http.*

/**
 * Graphql session context hold attributes such as the context user.
 */
internal class SessionContext(private val env: DataFetchingEnvironment) {
    private val tracer = Tracer.create<SessionContext>()

    @Suppress("MemberVisibilityCanBePrivate")
    fun getUser(): ContextUser? {
        return env.graphQlContext.getOrDefault(KEY_USER, null)
    }

    fun printUser() {
        val user: ContextUser? = getUser()

        user?.let {
            tracer.info("EUREKA! Found context user: ${it.user}.")
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
        fun injectUserFromHeader(map: MutableMap<String, Any>, headers: Headers) {
            headers[KEY_USER]?.let { value ->
                if (value.isNotBlank()) {
                    map[KEY_USER] = ContextUser(user = value)
                }
            }
        }
    }
}
