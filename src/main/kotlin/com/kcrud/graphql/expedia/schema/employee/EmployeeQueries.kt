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
import org.koin.mp.KoinPlatform.getKoin
import java.util.*

/**
 * Employee query definitions.
 */
@ExpediaAPI
class EmployeeQueries : Query {

    private val service: EmployeeService = getKoin().get()

    fun employee(employeeId: UUID): Employee? {
        return service.findById(employeeId = employeeId)
    }

    fun employees(): List<Employee> {
        return service.findAll()
    }
}
