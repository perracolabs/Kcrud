/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.api.graphql.frameworks.expedia

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.Schema
import com.expediagroup.graphql.server.ktor.*
import com.expediagroup.graphql.server.operations.Mutation
import com.expediagroup.graphql.server.operations.Query
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kcrud.base.admin.settings.config.sections.GraphQLSettings
import kcrud.base.api.graphql.frameworks.expedia.annotation.ExpediaAPI
import kcrud.base.api.graphql.frameworks.expedia.context.ContextFactory
import kcrud.base.api.graphql.frameworks.expedia.types.CustomSchemaGeneratorHooks
import kcrud.base.utils.NetworkUtils
import kcrud.base.utils.Tracer
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Sets up the GraphQL engine. Currently, using Expedia GraphQL.
 *
 * See: [Expedia GraphQL Documentation](https://opensource.expediagroup.com/graphql-kotlin/docs/)
 */
@ExpediaAPI
class ExpediaGraphQLSetup(
    private val application: Application,
    private val settings: GraphQLSettings,
    private val withSecurity: Boolean
) {
    private val tracer = Tracer<ExpediaGraphQLSetup>()
    private val graphqlPackages = listOf("kcrud")

    /**
     * Configures the GraphQL engine.
     *
     * @param queries The list of GraphQL queries to be configured.
     * @param mutations The list of GraphQL mutations to be configured.
     */
    fun configure(
        queries: List<Query>,
        mutations: List<Mutation>
    ) {
        tracer.info("Configuring ExpediaGroup GraphQL engine.")

        if (settings.playground) {
            tracer.byEnvironment(message = "GraphQL playground is enabled.")
        }

        installGraphQL(
            schemaDirectives = KcrudSchema(),
            queriesSchema = queries,
            mutationsSchema = mutations
        )

        dumpSchema(queriesSchema = queries, mutationsSchema = mutations)
        setEndpoints()

        val endpoints = if (settings.playground) listOf("graphiql", "sdl", "graphql") else listOf("sdl", "graphql")
        NetworkUtils.logEndpoints(
            reason = "GraphQL",
            endpoints = endpoints
        )
    }

    private fun installGraphQL(
        schemaDirectives: Schema,
        queriesSchema: List<Query>,
        mutationsSchema: List<Mutation>
    ) {
        application.install(GraphQL) {
            schema {
                packages = graphqlPackages
                queries = queriesSchema
                mutations = mutationsSchema
                schemaObject = schemaDirectives
                hooks = CustomSchemaGeneratorHooks()
            }

            server {
                contextFactory = ContextFactory()
            }
        }
    }

    private fun setEndpoints() {
        application.routing {
            if (withSecurity) {
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
            if (settings.playground) {
                graphiQLRoute() // http://localhost:8080/graphiql
            }
        }
    }

    private fun dumpSchema(queriesSchema: List<Query>, mutationsSchema: List<Mutation>) {
        if (!settings.dumpSchema || settings.schemaPath.isBlank())
            return

        tracer.byEnvironment(message = "Dumping GraphQL schema.")

        val topLevelQueries = queriesSchema.map { TopLevelObject(it) }
        val topLevelMutations = mutationsSchema.map { TopLevelObject(it) }

        val schema = toSchema(
            queries = topLevelQueries,
            mutations = topLevelMutations,
            config = SchemaGeneratorConfig(
                supportedPackages = graphqlPackages,
                hooks = CustomSchemaGeneratorHooks()
            )
        )

        val sdl: String = schema.print()
        val directoryPath: Path = Files.createDirectories(Paths.get(settings.schemaPath))
        val fileUri: URI = directoryPath.normalize().resolve("schema.graphql").toUri()
        val file = File(fileUri)
        file.writeText(text = sdl)

        tracer.info("Dumped GraphQL schema file:")
        tracer.info(file.absolutePath)
    }
}
