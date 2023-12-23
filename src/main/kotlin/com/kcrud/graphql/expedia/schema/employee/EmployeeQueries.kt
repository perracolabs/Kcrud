/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia.schema.employee

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.kcrud.data.entities.employee.Employee
import com.kcrud.data.pagination.Page
import com.kcrud.data.pagination.Pageable
import com.kcrud.data.repositories.employee.types.EmployeeFilterSet
import com.kcrud.data.repositories.employee.types.EmployeeSet
import com.kcrud.graphql.expedia.ExpediaAPI
import com.kcrud.graphql.expedia.context.SessionContext
import com.kcrud.services.EmployeeService
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
    suspend fun employees(env: DataFetchingEnvironment, pageable: Pageable? = null): EmployeeSet {
        // Example of how to get the user from the context.
        // See ContextFactory and SessionContext for more details.
        SessionContext(env = env).printUser()

        val page: Page<Employee> = service.findAll(pageable = pageable)
        return EmployeeSet(
            content = page.content,
            info = page.info
        )
    }

    @GraphQLDescription("Filterable paginated Employee query.")
    suspend fun filterEmployees(filterSet: EmployeeFilterSet, pageable: Pageable? = null): EmployeeSet {
        val page = service.filter(filterSet = filterSet, pageable = pageable)
        return EmployeeSet(
            content = page.content,
            info = page.info
        )
    }
}
