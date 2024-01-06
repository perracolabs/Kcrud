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
import kcrud.server.api.graphql.expedia.employee.EmployeeMutations
import kcrud.server.api.graphql.expedia.employee.EmployeeQueries
import kcrud.server.api.graphql.expedia.employment.EmploymentMutations
import kcrud.server.api.graphql.expedia.employment.EmploymentQueries
import kcrud.server.api.graphql.kgraphql.SharedTypes

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
    ExpediaGraphQLSetup().configure(
        application = application,
        queries = listOf(
            EmployeeQueries(),
            EmploymentQueries()
        ),
        mutations = listOf(
            EmployeeMutations(),
            EmploymentMutations()
        )
    )
}

@OptIn(KGraphQLAPI::class)
private fun configureKGraphQL(application: Application) {
    KGraphQLSetup().configure(application = application) { schemaBuilder ->
        SharedTypes(schemaBuilder = schemaBuilder)
            .configure()

        kcrud.server.api.graphql.kgraphql.employee.EmployeeQueries(schemaBuilder = schemaBuilder)
            .configureInputs()
            .configureTypes()
            .configureQueries()

        kcrud.server.api.graphql.kgraphql.employee.EmployeeMutations(schemaBuilder = schemaBuilder)
            .configureInputs()
            .configureMutations()

        kcrud.server.api.graphql.kgraphql.employment.EmploymentQueries(schemaBuilder = schemaBuilder)
            .configureTypes()
            .configureQueries()

        kcrud.server.api.graphql.kgraphql.employment.EmploymentMutations(schemaBuilder = schemaBuilder)
            .configureInputs()
            .configureMutations()
    }
}

