/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.kgraphql.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.data.models.employee.Employee
import com.kcrud.data.models.employee.EmployeeParams
import com.kcrud.graphql.kgraphql.KGraphQLAPI
import com.kcrud.services.EmployeeService
import org.koin.java.KoinJavaComponent.inject
import java.util.*


/**
 * Demonstrates modularization of GraphQL schemas for scalability.
 * This object serves as an example of how to modularize different components of a GraphQL schema.
 *
 * By following this pattern, it becomes easier to split a large and growing schema into separate
 * files for better maintainability.
 *
 * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
 */
@KGraphQLAPI
internal class EmployeeSchema(private val schemaBuilder: SchemaBuilder) {

    private val service: EmployeeService by inject(EmployeeService::class.java)

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
                resolver { employeeId: UUID -> service.findById(employeeId = employeeId) }
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
            inputType<EmployeeParams> {
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
