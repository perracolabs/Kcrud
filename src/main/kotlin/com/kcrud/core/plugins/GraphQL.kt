/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.plugins

import com.kcrud.core.admin.settings.AppSettings
import com.kcrud.core.api.graphql.frameworks.expedia.ExpediaGraphQLSetup
import com.kcrud.core.api.graphql.frameworks.kgraphql.KGraphQLSetup
import com.kcrud.core.api.graphql.utils.GraphQLFramework
import io.ktor.server.application.*

/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [Expedia GraphQL Documentation](https://opensource.expediagroup.com/graphql-kotlin/docs/)
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
fun Application.configureGraphQL() {

    if (!AppSettings.graphql.isEnabled) {
        return
    }

    when (AppSettings.graphql.framework) {
        GraphQLFramework.EXPEDIA_GROUP -> {
            ExpediaGraphQLSetup().configure(application = this)
        }

        GraphQLFramework.K_GRAPHQL -> {
            KGraphQLSetup().configure(application = this)
        }
    }
}


