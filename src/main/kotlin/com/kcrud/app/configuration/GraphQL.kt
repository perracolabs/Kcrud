package com.kcrud.app.configuration

import com.apurebase.kgraphql.GraphQL
import com.kcrud.data.graphql.GraphQLSchemas
import com.kcrud.data.repositories.EmployeeRepository
import io.ktor.server.application.*


/**
 * Application extension function to configure GraphQL.
 *
 * Currently, using KGraphQL engine.
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
fun Application.configureGraphQL() {
    install(GraphQL) {
        playground = true

        val repository = EmployeeRepository()

        schema {
            GraphQLSchemas.configureTypes(schemaBuilder = this)
            GraphQLSchemas.configureQueryTypes(schemaBuilder = this)
            GraphQLSchemas.configureQueries(schemaBuilder = this, repository = repository)
            GraphQLSchemas.configureMutationInputs(schemaBuilder = this)
            GraphQLSchemas.configureMutations(schemaBuilder = this, repository = repository)
        }
    }
}
