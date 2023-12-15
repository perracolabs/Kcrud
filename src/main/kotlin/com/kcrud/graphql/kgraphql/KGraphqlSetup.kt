/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.kgraphql

import com.apurebase.kgraphql.GraphQL
import com.kcrud.graphql.kgraphql.schema.EmployeeSchema
import com.kcrud.graphql.kgraphql.schema.EmploymentSchema
import com.kcrud.graphql.kgraphql.schema.SharedTypesSchema
import com.kcrud.security.AuthenticationToken
import com.kcrud.services.EmployeeService
import com.kcrud.services.EmploymentService
import io.ktor.server.application.*

/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
internal object KGraphQLSetup {
    @OptIn(KGraphQLAPI::class)
    fun configure(application: Application, employeeService: EmployeeService, employmentService: EmploymentService) {
        application.install(GraphQL) {

            playground = true  // Enable GraphQL playground for easier development and testing.

            // Set the security context to verify the JWT token for each incoming GraphQL request.
            context { call ->
                AuthenticationToken.verify(call)
            }

            // Define the GraphQL schema.
            schema {
                SharedTypesSchema(schemaBuilder = this)
                    .configure()

                EmployeeSchema(schemaBuilder = this, service = employeeService)
                    .configureQueryTypes()
                    .configureQueries()
                    .configureMutationInputs()
                    .configureMutations()

                EmploymentSchema(schemaBuilder = this, service = employmentService)
                    .configureQueryTypes()
                    .configureQueries()
                    .configureMutationInputs()
                    .configureMutations()
            }
        }
    }
}