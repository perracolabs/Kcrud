/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.kgraphql

import com.apurebase.kgraphql.GraphQL
import com.kcrud.graphql.kgraphql.context.SessionContext
import com.kcrud.graphql.kgraphql.schema.SharedTypes
import com.kcrud.graphql.kgraphql.schema.employee.EmployeeMutations
import com.kcrud.graphql.kgraphql.schema.employee.EmployeeQueries
import com.kcrud.graphql.kgraphql.schema.employment.EmploymentMutations
import com.kcrud.graphql.kgraphql.schema.employment.EmploymentQueries
import com.kcrud.security.authentication.AuthenticationToken
import io.ktor.server.application.*

/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
internal object KGraphQLSetup {
    @OptIn(KGraphQLAPI::class)
    fun configure(application: Application, withPlayground: Boolean): List<String>? {
        application.install(GraphQL) {

            // Set GraphQL playground for development and testing.
            playground = withPlayground

            // Set the security context to verify the JWT token for each incoming GraphQL request.
            context { call ->
                AuthenticationToken.verify(call)

                // Example of how to add a session user from the request headers to the context.
                // This could be for example be done by decoding a JWT token from the bearer key.
                // In this simple example is just a header key-value pair.
                SessionContext.injectUserFromHeader(contextBuilder = this, call = call)
            }

            // Define the GraphQL schema.
            schema {
                SharedTypes(schemaBuilder = this)
                    .configure()

                EmployeeQueries(schemaBuilder = this)
                    .configureInputs()
                    .configureTypes()
                    .configureQueries()

                EmployeeMutations(schemaBuilder = this)
                    .configureInputs()
                    .configureMutations()

                EmploymentQueries(schemaBuilder = this)
                    .configureTypes()
                    .configureQueries()

                EmploymentMutations(schemaBuilder = this)
                    .configureInputs()
                    .configureMutations()
            }
        }

        // Return the configured endpoints.
        return if (withPlayground) listOf("graphql") else null
    }
}
