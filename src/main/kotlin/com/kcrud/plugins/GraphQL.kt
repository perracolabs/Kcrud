/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.apurebase.kgraphql.GraphQL
import com.kcrud.data.graphql.GraphQLCommonSchema
import com.kcrud.data.graphql.GraphQLEmployeeSchema
import com.kcrud.data.graphql.GraphQLEmploymentSchema
import com.kcrud.security.AuthenticationToken
import com.kcrud.services.EmployeeService
import com.kcrud.services.EmploymentService
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
fun Application.configureGraphQL() {

    install(GraphQL) {
        playground = true  // Enable GraphQL playground for easier development and testing.

        // Set the security context to verify the JWT token for each incoming GraphQL request.
        context { call ->
            AuthenticationToken.verify(call)
        }

        val employeeService by inject<EmployeeService>()
        val employmentService by inject<EmploymentService>()

        // Define the GraphQL schema.
        schema {
            GraphQLCommonSchema(schemaBuilder = this)
                .configureCommonTypes()

            GraphQLEmployeeSchema(schemaBuilder = this, service = employeeService)
                .configureQueryTypes()
                .configureQueries()
                .configureMutationInputs()
                .configureMutations()

            GraphQLEmploymentSchema(schemaBuilder = this, service = employmentService)
                .configureQueryTypes()
                .configureQueries()
                .configureMutationInputs()
                .configureMutations()
        }
    }
}


