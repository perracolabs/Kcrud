/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.graphql.kgraphql.schema.employee

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.api.graphql.kgraphql.KGraphQLAPI
import com.kcrud.domain.entities.employee.EmployeeRequest
import com.kcrud.domain.services.EmployeeService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

/**
 * Employee mutation definitions.
 *
 * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
 */
@KGraphQLAPI
internal class EmployeeMutations(private val schemaBuilder: SchemaBuilder) : KoinComponent {

    private val service: EmployeeService by inject()

    /**
     * Configures input types for mutations.
     */
    fun configureInputs(): EmployeeMutations {
        schemaBuilder.apply {
            inputType<EmployeeRequest> {
                name = "Input type definition for Employee."
            }
        }

        return this
    }

    /**
     * Configures mutation resolvers to modify data.
     */
    fun configureMutations(): EmployeeMutations {
        schemaBuilder.apply {
            mutation("createEmployee") {
                description = "Creates a new employee."
                resolver { employee: EmployeeRequest -> service.create(employeeRequest = employee) }
            }

            mutation("updateEmployee") {
                description = "Updates an existing employee."
                resolver { employeeId: UUID, employee: EmployeeRequest ->
                    service.update(employeeId = employeeId, employeeRequest = employee)
                }
            }

            mutation("deleteEmployee") {
                description = "Deletes an existing employee."
                resolver { employeeId: UUID -> service.delete(employeeId = employeeId) }
            }

            mutation("deleteAllEmployees") {
                description = "Deletes all existing employees."
                resolver { -> service.deleteAll() }
            }
        }

        return this
    }
}
