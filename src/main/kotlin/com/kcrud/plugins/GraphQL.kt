package com.kcrud.plugins

import com.apurebase.kgraphql.GraphQL
import com.kcrud.data.graphql.GraphQLSchemas
import com.kcrud.data.repositories.IEmployeeRepository
import com.kcrud.utils.Security
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
            Security.verifyToken(call)
        }

        val employeeRepository: IEmployeeRepository by inject()

        // Define the GraphQL schema.
        schema {
            GraphQLSchemas(schemaBuilder = this, repository = employeeRepository)
                .configureCommonTypes()
                .configureQueryTypes()
                .configureQueries()
                .configureMutationInputs()
                .configureMutations()
        }
    }
}


