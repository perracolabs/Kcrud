/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.kgraphql.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.data.models.employee.Employee
import com.kcrud.data.models.employee.EmployeeInput
import com.kcrud.services.EmployeeService
import com.kcrud.utils.toUUID


/**
 * Demonstrates modularization of GraphQL schemas for scalability.
 * This object serves as an example of how to modularize different components of a GraphQL schema.
 *
 * By following this pattern, it becomes easier to split a large and growing schema into separate
 * files for better maintainability.
 *
 * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
 * @param service The service used in mutation resolvers.
 */
class EmployeeSchema(private val schemaBuilder: SchemaBuilder, private val service: EmployeeService) {

    /**
     * Configures query types specifically.
     */
    fun configureQueryTypes(): EmployeeSchema {
        schemaBuilder.apply {
            type<Employee> {
                description = "Query type definition for employee."
            }
        }

        return this
    }

    /**
     * Configures query resolvers to fetch data.
     */
    fun configureQueries(): EmployeeSchema {
        schemaBuilder.apply {
            query("employee") {
                description = "Returns a single employee given its id."
                resolver { employeeId: String -> service.findById(employeeId = employeeId.toUUID()) }
            }
            query("employees") {
                description = "Returns all existing employees."
                resolver { -> service.findAll() }
            }
        }

        return this
    }

    /**
     * Configures input types for mutations.
     */
    fun configureMutationInputs(): EmployeeSchema {
        schemaBuilder.apply {
            inputType<EmployeeInput> {
                name = "Input type definition for Employee."
            }
        }

        return this
    }

    /**
     * Configures mutation resolvers to modify data.
     */
    fun configureMutations(): EmployeeSchema {
        schemaBuilder.apply {
            mutation("createEmployee") {
                description = "Creates a new employee."
                resolver { employee: EmployeeInput -> service.create(employee = employee) }
            }

            mutation("updateEmployee") {
                description = "Updates an existing employee."
                resolver { employeeId: String, employee: EmployeeInput ->
                    service.update(employeeId = employeeId.toUUID(), employee = employee)
                }
            }

            mutation("deleteEmployee") {
                description = "Deletes an existing employee."
                resolver { employeeId: String -> service.delete(employeeId = employeeId.toUUID()) }
            }

            mutation("deleteAllEmployees") {
                description = "Deletes all existing employees."
                resolver { -> service.deleteAll() }
            }
        }

        return this
    }
}
