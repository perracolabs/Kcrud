/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia.schema.employee

import com.expediagroup.graphql.server.operations.Mutation
import com.kcrud.data.models.employee.Employee
import com.kcrud.data.models.employee.EmployeeParams
import com.kcrud.graphql.expedia.ExpediaAPI
import com.kcrud.services.EmployeeService
import org.koin.mp.KoinPlatform.getKoin
import java.util.*

/**
 * Employee mutation definitions.
 */
@Suppress("unused")
@ExpediaAPI
class EmployeeMutations : Mutation {

    private val service: EmployeeService = getKoin().get()

    fun createEmployee(employee: EmployeeParams): Employee {
        return service.create(employee = employee)
    }

    fun updateEmployee(employeeId: UUID, employee: EmployeeParams): Employee? {
        return service.update(employeeId = employeeId, employee = employee)
    }

    fun deleteEmployee(employeeId: UUID): Int {
        return service.delete(employeeId = employeeId)
    }

    fun deleteAllEmployees(): Int {
        return service.deleteAll()
    }
}
