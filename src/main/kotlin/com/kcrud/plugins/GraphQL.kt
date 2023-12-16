/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.graphql.GraphQLFramework
import com.kcrud.graphql.expedia.ExpediaGraphQLSetup
import com.kcrud.graphql.kgraphql.KGraphQLSetup
import com.kcrud.services.EmployeeService
import com.kcrud.services.EmploymentService
import com.kcrud.settings.SettingsProvider
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
fun Application.graphQLModule() {
    val employeeService by inject<EmployeeService>()
    val employmentService by inject<EmploymentService>()

    val framework = SettingsProvider.get.graphql.framework
    val withPlayground = SettingsProvider.get.graphql.playground

    when (framework) {
        GraphQLFramework.EXPEDIA_GROUP -> {
            ExpediaGraphQLSetup.configure(
                application = this,
                withPlayground = withPlayground,
                employeeService = employeeService,
                employmentService = employmentService
            )
        }

        GraphQLFramework.K_GRAPHQL -> {
            KGraphQLSetup.configure(
                application = this,
                withPlayground = withPlayground,
                employeeService = employeeService,
                employmentService = employmentService
            )
        }
    }
}


