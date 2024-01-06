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
import kcrud.base.admin.settings.AppSettings
import kcrud.base.api.graphql.frameworks.kgraphql.annotation.KGraphQLAPI
import kcrud.base.api.graphql.frameworks.kgraphql.context.SessionContext
import kcrud.base.utils.NetworkUtils
import kcrud.base.utils.Tracer

/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
class KGraphQLSetup {
    private val tracer = Tracer<KGraphQLSetup>()

    /**
     * Configures the GraphQL engine.
     *
     * @param application The application pipeline.
     * @param configureSchema The lambda function to configure the GraphQL schema.
     */
    @OptIn(KGraphQLAPI::class)
    fun configure(application: Application, configureSchema: (SchemaBuilder) -> Unit) {
        val withPlayground = AppSettings.graphql.playground
        tracer.info("Configuring KGraphQL engine.")

        if (withPlayground) {
            tracer.byEnvironment(message = "GraphQL playground is enabled.")
        }

        application.install(GraphQL) {

            // Set GraphQL playground for development and testing.
            playground = withPlayground

            // Set the security context to verify the JWT token for each incoming GraphQL request.
            context { call ->
                AuthenticationToken.verify(call)

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

        if (withPlayground) {
            NetworkUtils.logEndpoints(
                reason = "GraphQL",
                endpoints = listOf("graphql")
            )
        }
    }
}
