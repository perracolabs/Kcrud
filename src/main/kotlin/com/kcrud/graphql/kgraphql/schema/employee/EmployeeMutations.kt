/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.kgraphql.schema.employee

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.data.entities.employee.EmployeeParams
import com.kcrud.graphql.kgraphql.KGraphQLAPI
import com.kcrud.services.EmployeeService
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
            inputType<EmployeeParams> {
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
                resolver { employee: EmployeeParams -> service.create(employee = employee) }
            }

            mutation("updateEmployee") {
                description = "Updates an existing employee."
                resolver { employeeId: UUID, employee: EmployeeParams ->
                    service.update(employeeId = employeeId, employee = employee)
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
