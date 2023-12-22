/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia.schema.employee

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.kcrud.data.entities.employee.Employee
import com.kcrud.data.database.shared.Pagination
import com.kcrud.data.repositories.employee.EmployeeFilterSet
import com.kcrud.graphql.expedia.ExpediaAPI
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
    fun employees(pagination: Pagination?): List<Employee> {
        return service.findAll(pagination = pagination)
    }

    @GraphQLDescription("Filterable paginated Employee query.")
    fun filterEmployees(filterSet: EmployeeFilterSet, pagination: Pagination): List<Employee> {
        return service.filter(filterSet = filterSet, pagination = pagination)
    }
}
