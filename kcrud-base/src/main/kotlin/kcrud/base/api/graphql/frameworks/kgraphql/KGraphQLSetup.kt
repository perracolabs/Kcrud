/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.api.graphql.frameworks.kgraphql

import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import io.ktor.server.application.*
import kcrud.base.admin.env.security.authentication.AuthenticationToken
import kcrud.base.admin.settings.config.sections.GraphQLSettings
import kcrud.base.api.graphql.frameworks.kgraphql.annotation.KGraphQLAPI
import kcrud.base.api.graphql.frameworks.kgraphql.context.SessionContext
import kcrud.base.utils.NetworkUtils
import kcrud.base.utils.Tracer

/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
class KGraphQLSetup(
    private val application: Application,
    private val settings: GraphQLSettings,
    private val withSecurity: Boolean
) {
    private val tracer = Tracer<KGraphQLSetup>()

    /**
     * Configures the GraphQL engine.
     *
     * @param configureSchema The lambda function to configure the GraphQL schema.
     */
    @OptIn(KGraphQLAPI::class)
    fun configure(configureSchema: (SchemaBuilder) -> Unit) {
        tracer.info("Configuring KGraphQL engine.")

        if (settings.playground) {
            tracer.byEnvironment(message = "GraphQL playground is enabled.")
        }

        application.install(GraphQL) {

            // Set GraphQL playground for development and testing.
            playground = settings.playground

            // Set the security context to verify the JWT token for each incoming GraphQL request.
            context { call ->
                if (withSecurity) {
                    AuthenticationToken.verify(call = call)
                }

                // Example of how to add a session user from the request headers to the context.
                // This could be for example be done by decoding a JWT token from the bearer key.
                // In this simple example is just a header key-value pair.
                SessionContext.injectUserFromHeader(contextBuilder = this, call = call)
            }

            // Define the GraphQL schema.
            schema {
                configureSchema(this)
            }
        }

        if (settings.playground) {
            NetworkUtils.logEndpoints(
                reason = "GraphQL",
                endpoints = listOf("graphql")
            )
        }
    }
}
