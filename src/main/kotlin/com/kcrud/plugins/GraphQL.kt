/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.graphql.GraphQLFramework
import com.kcrud.graphql.expedia.ExpediaGraphQLSetup
import com.kcrud.graphql.kgraphql.KGraphQLSetup
import com.kcrud.settings.SettingsProvider
import com.kcrud.utils.NetworkUtils
import io.ktor.server.application.*

/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [Expedia GraphQL Documentation](https://opensource.expediagroup.com/graphql-kotlin/docs/)
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
fun Application.graphQLModule() {

    if (!SettingsProvider.graphql.isEnabled) {
        return
    }

    val framework = SettingsProvider.graphql.framework
    val withPlayground = SettingsProvider.graphql.playground

    val endpoints: List<String>? = when (framework) {
        GraphQLFramework.EXPEDIA_GROUP -> {
            ExpediaGraphQLSetup.configure(
                application = this,
                withPlayground = withPlayground
            )
        }

        GraphQLFramework.K_GRAPHQL -> {
            KGraphQLSetup.configure(
                application = this,
                withPlayground = withPlayground
            )
        }
    }

    endpoints?.let { list ->
        NetworkUtils.logEndpoints(
            reason = "GraphQL",
            endpoints = list
        )
    }
}


