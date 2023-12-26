/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.graphql.frameworks.expedia

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.ktor.*
import com.kcrud.api.graphql.frameworks.expedia.context.ContextFactory
import com.kcrud.api.graphql.frameworks.expedia.schema.KcrudSchema
import com.kcrud.api.graphql.frameworks.expedia.schema.employee.EmployeeMutations
import com.kcrud.api.graphql.frameworks.expedia.schema.employee.EmployeeQueries
import com.kcrud.api.graphql.frameworks.expedia.schema.employment.EmploymentMutations
import com.kcrud.api.graphql.frameworks.expedia.schema.employment.EmploymentQueries
import com.kcrud.api.graphql.frameworks.expedia.types.CustomSchemaGeneratorHooks
import com.kcrud.config.settings.AppSettings
import com.kcrud.utils.NetworkUtils
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
internal class ExpediaGraphQLSetup {
    private val tracer = Tracer<ExpediaGraphQLSetup>()
    private val graphqlPackages = listOf("com.kcrud")

    fun configure(application: Application) {
        val withPlayground = AppSettings.graphql.playground
        tracer.info("Configuring ExpediaGroup GraphQL engine.")

        if (withPlayground) {
            tracer.byDeploymentType(message = "GraphQL playground is enabled.")
        }

        installGraphQL(application = application)
        dumpSchema()
        setEndpoints(application = application, withPlayground = withPlayground)

        val endpoints = if (withPlayground) listOf("graphiql", "sdl", "graphql") else listOf("sdl", "graphql")
        NetworkUtils.logEndpoints(
            reason = "GraphQL",
            endpoints = endpoints
        )
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

            server {
                contextFactory = ContextFactory()
            }
        }
    }

    private fun setEndpoints(application: Application, withPlayground: Boolean) {
        application.routing {
            if (AppSettings.security.jwt.isEnabled) {
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
        if (!AppSettings.graphql.dumpSchema)
            return

        tracer.byDeploymentType(message = "Dumping GraphQL schema.")

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

        tracer.info("Dumped GraphQL schema file:")
        tracer.info(file.absolutePath)
    }
}
