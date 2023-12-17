/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia

import com.expediagroup.graphql.server.ktor.*
import com.kcrud.graphql.expedia.schema.KcrudSchema
import com.kcrud.graphql.expedia.schema.employee.EmployeeMutations
import com.kcrud.graphql.expedia.schema.employee.EmployeeQueries
import com.kcrud.graphql.expedia.schema.employment.EmploymentMutations
import com.kcrud.graphql.expedia.schema.employment.EmploymentQueries
import com.kcrud.graphql.expedia.types.CustomSchemaGeneratorHooks
import com.kcrud.settings.SettingsProvider
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

/**
 * Sets up the GraphQL engine. Currently, using Expedia GraphQL.
 *
 * See: [Expedia GraphQL Documentation](https://opensource.expediagroup.com/graphql-kotlin/docs/)
 */
internal object ExpediaGraphQLSetup {
    @OptIn(ExpediaAPI::class)
    fun configure(application: Application, withPlayground: Boolean): List<String> {
        application.install(GraphQL) {

            schema {
                this.packages = listOf("com.kcrud")
                queries = listOf(
                    EmployeeQueries(),
                    EmploymentQueries()
                )
                mutations = listOf(
                    EmployeeMutations(),
                    EmploymentMutations()
                )
                schemaObject = KcrudSchema()
                hooks = CustomSchemaGeneratorHooks()
            }
        }

        application.routing {
            if (SettingsProvider.security.jwt.isEnabled) {
                authenticate {
                    graphQLGetRoute()
                    graphQLPostRoute()
                }
            } else {
                graphQLGetRoute()
                graphQLPostRoute()
            }

            graphQLSDLRoute() // http://localhost:8080/sdl

            // Set GraphQL playground for development and testing.
            if (withPlayground) {
                graphiQLRoute() // http://localhost:8080/graphiql
            }
        }

        // Return the configured endpoints.
        return if (withPlayground) listOf("graphiql", "sdl", "graphql") else listOf("sdl", "graphql")
    }
}
