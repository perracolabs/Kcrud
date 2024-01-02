/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.graphql.frameworks.expedia.schema.employee

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.kcrud.api.graphql.frameworks.expedia.annotation.ExpediaAPI
import com.kcrud.api.graphql.frameworks.expedia.context.SessionContext
import com.kcrud.data.utils.pagination.Page
import com.kcrud.data.utils.pagination.Pageable
import com.kcrud.domain.entities.employee.Employee
import com.kcrud.domain.entities.employee.EmployeeConnection
import com.kcrud.domain.entities.employee.EmployeeFilterSet
import com.kcrud.domain.services.EmployeeService
import graphql.schema.DataFetchingEnvironment
import org.koin.mp.KoinPlatform.getKoin
import java.util.*

/**
 * Employee query definitions.
 */
@Suppress("unused", "RedundantSuspendModifier")
@ExpediaAPI
class EmployeeQueries : Query {
    private val service: EmployeeService = getKoin().get()

    @GraphQLDescription("Returns an employee given its id.")
    suspend fun employee(employeeId: UUID): Employee? {
        return service.findById(employeeId = employeeId)
    }

    @GraphQLDescription("Returns all existing employees.")
    suspend fun employees(env: DataFetchingEnvironment, pageable: Pageable? = null): EmployeeConnection {
        // Example of how to get the user from the context.
        // See ContextFactory and SessionContext for more details.
        SessionContext(env = env).printUser()

        val page: Page<Employee> = service.findAll(pageable = pageable)
        return EmployeeConnection(
            content = page.content,
            info = page.info
        )
    }

    @GraphQLDescription("Filterable paginated Employee query.")
    suspend fun filterEmployees(filterSet: EmployeeFilterSet, pageable: Pageable? = null): EmployeeConnection {
        val page = service.filter(filterSet = filterSet, pageable = pageable)
        return EmployeeConnection(
            content = page.content,
            info = page.info
        )
    }
}
