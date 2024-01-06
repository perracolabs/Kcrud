/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.plugins

import io.ktor.server.application.*
import kcrud.base.admin.settings.AppSettings
import kcrud.base.api.graphql.GraphQLFramework
import kcrud.base.api.graphql.frameworks.expedia.ExpediaGraphQLSetup
import kcrud.base.api.graphql.frameworks.expedia.annotation.ExpediaAPI
import kcrud.base.api.graphql.frameworks.kgraphql.KGraphQLSetup
import kcrud.base.api.graphql.frameworks.kgraphql.annotation.KGraphQLAPI
import kcrud.server.api.graphql.kgraphql.SharedTypes
import kcrud.server.api.graphql.expedia.employee.EmployeeMutations as ExpediaEmployeeMutations
import kcrud.server.api.graphql.expedia.employee.EmployeeQueries as ExpediaEmployeeQueries
import kcrud.server.api.graphql.expedia.employment.EmploymentMutations as ExpediaEmploymentMutations
import kcrud.server.api.graphql.expedia.employment.EmploymentQueries as ExpediaEmploymentQueries
import kcrud.server.api.graphql.kgraphql.employee.EmployeeMutations as KGraphQLEmployeeMutations
import kcrud.server.api.graphql.kgraphql.employee.EmployeeQueries as KGraphQLEmployeeQueries
import kcrud.server.api.graphql.kgraphql.employment.EmploymentMutations as KGraphQLEmploymentMutations
import kcrud.server.api.graphql.kgraphql.employment.EmploymentQueries as KGraphQLEmploymentQueries

/**
 * Sets up the GraphQL engine. Supported libraries are 'Expedia GraphQL' and 'KGraphQL'.
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
            configureExpedia(application = this)
        }

        GraphQLFramework.K_GRAPHQL -> {
            configureKGraphQL(application = this)
        }
    }
}

@OptIn(ExpediaAPI::class)
private fun configureExpedia(application: Application) {
    ExpediaGraphQLSetup(
        application = application,
        settings = AppSettings.graphql,
        withSecurity = AppSettings.security.jwt.isEnabled
    ).configure(
        queries = listOf(
            ExpediaEmployeeQueries(),
            ExpediaEmploymentQueries()
        ),
        mutations = listOf(
            ExpediaEmployeeMutations(),
            ExpediaEmploymentMutations()
        )
    )
}

@OptIn(KGraphQLAPI::class)
private fun configureKGraphQL(application: Application) {
    KGraphQLSetup(
        application = application,
        settings = AppSettings.graphql,
        withSecurity = AppSettings.security.jwt.isEnabled
    ).configure { schemaBuilder ->
        SharedTypes(schemaBuilder = schemaBuilder)
            .configure()

        KGraphQLEmployeeQueries(schemaBuilder = schemaBuilder)
            .configureInputs()
            .configureTypes()
            .configureQueries()

        KGraphQLEmployeeMutations(schemaBuilder = schemaBuilder)
            .configureInputs()
            .configureMutations()

        KGraphQLEmploymentQueries(schemaBuilder = schemaBuilder)
            .configureTypes()
            .configureQueries()

        KGraphQLEmploymentMutations(schemaBuilder = schemaBuilder)
            .configureInputs()
            .configureMutations()
    }
}
