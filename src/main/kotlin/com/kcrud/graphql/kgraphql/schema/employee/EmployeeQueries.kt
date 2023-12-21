/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.kgraphql.schema.employee

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.data.entities.employee.Employee
import com.kcrud.graphql.kgraphql.KGraphQLAPI
import com.kcrud.services.EmployeeService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


/**
 * Employee query definitions.
 *
 * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
 */
@KGraphQLAPI
internal class EmployeeQueries(private val schemaBuilder: SchemaBuilder) : KoinComponent {

    private val service: EmployeeService by inject()

    /**
     * Configures query types specifically.
     */
    fun configureTypes(): EmployeeQueries {
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
    fun configureQueries(): EmployeeQueries {
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
}
