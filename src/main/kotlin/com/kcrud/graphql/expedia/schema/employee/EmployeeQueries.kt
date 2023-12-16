/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia.schema.employee

import com.expediagroup.graphql.server.operations.Query
import com.kcrud.data.models.employee.Employee
import com.kcrud.graphql.expedia.ExpediaAPI
import com.kcrud.services.EmployeeService
import java.util.*

/**
 * Employee query definitions.
 *
 * @param service The service used in query resolvers.
 */
@ExpediaAPI
class EmployeeQueries(private val service: EmployeeService) : Query {

    fun employee(employeeId: UUID): Employee? {
        return service.findById(employeeId = employeeId)
    }

    fun employees(): List<Employee> {
        return service.findAll()
    }
}
