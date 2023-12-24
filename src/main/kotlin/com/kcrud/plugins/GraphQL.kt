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
import io.ktor.server.application.*

/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [Expedia GraphQL Documentation](https://opensource.expediagroup.com/graphql-kotlin/docs/)
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
fun Application.configureGraphQL() {

    if (!SettingsProvider.graphql.isEnabled) {
        return
    }

    when (SettingsProvider.graphql.framework) {
        GraphQLFramework.EXPEDIA_GROUP -> {
            ExpediaGraphQLSetup().configure(application = this)
        }

        GraphQLFramework.K_GRAPHQL -> {
            KGraphQLSetup().configure(application = this)
        }
    }
}


