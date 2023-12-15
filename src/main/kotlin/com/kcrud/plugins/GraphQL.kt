/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.graphql.kgraphql.KGraphQLSetup
import io.ktor.server.application.*

/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
fun Application.configureGraphQL() {
    KGraphQLSetup.configure(application = this)
}


