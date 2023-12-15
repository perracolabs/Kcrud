/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.graphql.kgraphql.KGraphQLSetup
import com.kcrud.services.EmployeeService
import com.kcrud.services.EmploymentService
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

/**
 * Sets up the GraphQL engine. Currently, using KGraphQL.
 *
 * See: [KGraphQL Documentation](https://kgraphql.io/)
 */
fun Application.configureGraphQL() {
    val employeeService by inject<EmployeeService>()
    val employmentService by inject<EmploymentService>()

    KGraphQLSetup.configure(
        application = this,
        employeeService = employeeService,
        employmentService = employmentService
    )
}


