/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.kgraphql

import com.apurebase.kgraphql.GraphQL
import com.kcrud.graphql.kgraphql.schema.SharedTypes
import com.kcrud.graphql.kgraphql.schema.employee.EmployeeMutations
import com.kcrud.graphql.kgraphql.schema.employee.EmployeeQueries
import com.kcrud.graphql.kgraphql.schema.employment.EmploymentMutations
import com.kcrud.graphql.kgraphql.schema.employment.EmploymentQueries
import com.kcrud.security.AuthenticationToken
import com.kcrud.services.EmployeeService
import com.kcrud.services.EmploymentService
import com.kcrud.utils.NetworkUtils
import io.ktor.server.application.*

/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
internal object KGraphQLSetup {
    @OptIn(KGraphQLAPI::class)
    fun configure(
        application: Application,
        withPlayground: Boolean,
        employeeService: EmployeeService,
        employmentService: EmploymentService
    ) {
        application.install(GraphQL) {

            // Set GraphQL playground for development and testing.
            playground = withPlayground

            // Set the security context to verify the JWT token for each incoming GraphQL request.
            context { call ->
                AuthenticationToken.verify(call)
            }

            // Define the GraphQL schema.
            schema {
                SharedTypes(schemaBuilder = this)
                    .configure()

                EmployeeQueries(schemaBuilder = this, service = employeeService)
                    .configureTypes()
                    .configureQueries()

                EmployeeMutations(schemaBuilder = this, service = employeeService)
                    .configureInputs()
                    .configureMutations()

                EmploymentQueries(schemaBuilder = this, service = employmentService)
                    .configureTypes()
                    .configureQueries()

                EmploymentMutations(schemaBuilder = this, service = employmentService)
                    .configureInputs()
                    .configureMutations()
            }

            // Log the GraphQL endpoints.
            if (withPlayground) {
                NetworkUtils.logEndpoints(
                    reason = "Configured GraphQL endpoints.",
                    endpoints = listOf("graphql")
                )
            }
        }
    }
}
