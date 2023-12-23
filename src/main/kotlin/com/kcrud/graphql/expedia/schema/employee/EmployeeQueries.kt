/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia.schema.employee

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.server.operations.Query
import com.kcrud.data.entities.employee.Employee
import com.kcrud.data.pagination.Page
import com.kcrud.data.pagination.Pageable
import com.kcrud.data.repositories.employee.EmployeeFilterSet
import com.kcrud.graphql.expedia.ExpediaAPI
import com.kcrud.graphql.shared.EmployeeConnection
import com.kcrud.services.EmployeeService
import org.koin.mp.KoinPlatform.getKoin
import java.util.*

/**
 * Employee query definitions.
 */
@Suppress("unused")
@ExpediaAPI
class EmployeeQueries : Query {

    private val service: EmployeeService = getKoin().get()

    @GraphQLDescription("Returns an employee given its id.")
    fun employee(employeeId: UUID): Employee? {
        return service.findById(employeeId = employeeId)
    }

    @GraphQLDescription("Returns all existing employees.")
    fun employees(pageable: OptionalInput<Pageable>): EmployeeConnection {
        val pageableInfo = Pageable.fromOptionalPageable(pageable = pageable)
        val page: Page<Employee> = service.findAll(pageable = pageableInfo)
        return EmployeeConnection(
            content = page.content,
            pageInfo = page.pageInfo
        )
    }

    @GraphQLDescription("Filterable paginated Employee query.")
    fun filterEmployees(filterSet: EmployeeFilterSet, pageable: OptionalInput<Pageable>): EmployeeConnection {
        val pageableInfo = Pageable.fromOptionalPageable(pageable = pageable)
        val page = service.filter(filterSet = filterSet, pageable = pageableInfo)
        return EmployeeConnection(
            content = page.content,
            pageInfo = page.pageInfo
        )
    }
}
