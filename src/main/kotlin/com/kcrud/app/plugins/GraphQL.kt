package com.kcrud.app.plugins

import com.apurebase.kgraphql.GraphQL
import com.kcrud.data.graphql.GraphQLSchemas
import com.kcrud.data.repositories.EmployeeRepository
import com.kcrud.utils.Security
import io.ktor.server.application.*


/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/).
 */
fun Application.configureGraphQL() {
    install(GraphQL) {
        playground = true  // Enable GraphQL playground for easier development and testing.

        // Set the security context to verify the JWT token for each incoming GraphQL request.
        context { call ->
            Security().verifyToken(call)
        }

        // Initialize the Employee repository.
        val repository = EmployeeRepository()

        // Define the GraphQL schema.
        schema {
            GraphQLSchemas.configureCommonTypes(schemaBuilder = this)
            GraphQLSchemas.configureQueryTypes(schemaBuilder = this)
            GraphQLSchemas.configureQueries(schemaBuilder = this, repository = repository)
            GraphQLSchemas.configureMutationInputs(schemaBuilder = this)
            GraphQLSchemas.configureMutations(schemaBuilder = this, repository = repository)
        }
    }
}


