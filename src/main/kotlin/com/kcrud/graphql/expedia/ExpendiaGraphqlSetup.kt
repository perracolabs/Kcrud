/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.ktor.*
import com.kcrud.graphql.expedia.schema.KcrudSchema
import com.kcrud.graphql.expedia.schema.employee.EmployeeMutations
import com.kcrud.graphql.expedia.schema.employee.EmployeeQueries
import com.kcrud.graphql.expedia.schema.employment.EmploymentMutations
import com.kcrud.graphql.expedia.schema.employment.EmploymentQueries
import com.kcrud.graphql.expedia.types.CustomSchemaGeneratorHooks
import com.kcrud.settings.SettingsProvider
import com.kcrud.utils.Tracer
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Sets up the GraphQL engine. Currently, using Expedia GraphQL.
 *
 * See: [Expedia GraphQL Documentation](https://opensource.expediagroup.com/graphql-kotlin/docs/)
 */
@OptIn(ExpediaAPI::class)
internal object ExpediaGraphQLSetup {
    private val graphqlPackages = listOf("com.kcrud")

    fun configure(application: Application, withPlayground: Boolean): List<String> {

        installGraphQL(application = application)
        dumpSchema()
        setEndpoints(application = application, withPlayground = withPlayground)

        return if (withPlayground) listOf("graphiql", "sdl", "graphql") else listOf("sdl", "graphql")
    }

    private fun installGraphQL(application: Application) {
        application.install(GraphQL) {
            schema {
                packages = graphqlPackages
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
    }

    private fun setEndpoints(application: Application, withPlayground: Boolean) {
        // Configure the GraphQL endpoints.
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
    }

    private fun dumpSchema() {
        if (!SettingsProvider.graphql.dumpSchema)
            return

        val schema = toSchema(
            queries = listOf(
                TopLevelObject(EmployeeQueries()),
                TopLevelObject(EmploymentQueries())
            ),
            mutations = listOf(
                TopLevelObject(EmployeeMutations()),
                TopLevelObject(EmploymentMutations())
            ),
            config = SchemaGeneratorConfig(
                supportedPackages = graphqlPackages,
                hooks = CustomSchemaGeneratorHooks()
            )
        )

        val sdl = schema.print()
        val directoryPath = Files.createDirectories(Paths.get(".graphql"))
        val file = File(directoryPath.resolve("schema.graphql").toUri())
        file.writeText(text = sdl)

        val tracer = Tracer.create<ExpediaGraphQLSetup>()
        tracer.info("Dumped GraphQL schema file:")
        tracer.info(file.absolutePath)
    }
}

