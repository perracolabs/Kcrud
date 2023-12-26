/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.graphql.expedia.context

import com.expediagroup.graphql.generator.extensions.plus
import com.expediagroup.graphql.server.ktor.DefaultKtorGraphQLContextFactory
import com.kcrud.api.graphql.expedia.ExpediaAPI
import graphql.GraphQLContext
import io.ktor.server.request.*

/**
 * Custom GraphQL context factory that adds a session context to the context.
 *
 * Then a query or mutation can access the session context by using the DataFetchingEnvironment.
 * See EmployeeQueries.kt for an example of how to access the session context.
 *
 * Sample:
 * ```
 *  fun yourQuery(env: DataFetchingEnvironment, ...some parameters...): SomeData {
 *       val user: String? =  SessionContext(env=env).getUser()
 *       if (!user.isNullOrBlank()) {
 *           tracer.info("EUREKA! Found context user: $user.")
 *       }
 *       ...
 * ```
 *
 * See: [Expedia GraphQL Documentation](https://opensource.expediagroup.com/graphql-kotlin/docs/schema-generator/execution/contextual-data/)
 */
@ExpediaAPI
class ContextFactory : DefaultKtorGraphQLContextFactory() {
    override suspend fun generateContext(request: ApplicationRequest): GraphQLContext {
        // Example of how to add a session user from the request headers to the context.
        // This could be for example be done by decoding a JWT token from the bearer key.
        // In this simple example is just a header key-value pair.
        val map = mutableMapOf<String, Any>()
        SessionContext.injectUserFromHeader(map = map, headers = request.headers)

        return super.generateContext(request).plus(map)
    }
}
