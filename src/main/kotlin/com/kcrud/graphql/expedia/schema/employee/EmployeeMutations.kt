/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia.schema.employee

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.kcrud.data.entities.employee.Employee
import com.kcrud.data.entities.employee.EmployeeParams
import com.kcrud.graphql.expedia.ExpediaAPI
import com.kcrud.services.EmployeeService
import org.koin.mp.KoinPlatform.getKoin
import java.util.*

/**
 * Employee mutation definitions.
 */
@Suppress("unused", "RedundantSuspendModifier")
@ExpediaAPI
class EmployeeMutations : Mutation {
    private val service: EmployeeService = getKoin().get()

    @GraphQLDescription("Creates a new employee.")
    suspend fun createEmployee(employee: EmployeeParams): Employee {
        return service.create(employee = employee)
    }

    @GraphQLDescription("Updates an existing employee.")
    suspend fun updateEmployee(employeeId: UUID, employee: EmployeeParams): Employee? {
        return service.update(employeeId = employeeId, employee = employee)
    }

    @GraphQLDescription("Deletes an existing employee.")
    suspend fun deleteEmployee(employeeId: UUID): Int {
        return service.delete(employeeId = employeeId)
    }

    @GraphQLDescription("Delete all employees.")
    suspend fun deleteAllEmployees(): Int {
        return service.deleteAll()
    }
}
