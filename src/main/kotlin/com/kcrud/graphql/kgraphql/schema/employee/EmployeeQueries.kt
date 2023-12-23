/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.kgraphql.schema.employee

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.data.entities.employee.Employee
import com.kcrud.data.pagination.Page
import com.kcrud.data.pagination.Pageable
import com.kcrud.data.repositories.employee.types.EmployeeFilterSet
import com.kcrud.data.repositories.employee.types.EmployeeSet
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
     * Configures input types for queries.
     */
    fun configureInputs(): EmployeeQueries {
        schemaBuilder.apply {
            inputType<EmployeeFilterSet> {
                name = "Input type definition for employee filters."
            }
            inputType<Pageable> {
                name = "Input type definition for pagination."
            }
        }

        return this
    }

    /**
     * Configures query types specifically.
     */
    fun configureTypes(): EmployeeQueries {
        schemaBuilder.apply {
            type<Employee> {
                description = "Query type definition for employee."
            }
            type<EmployeeSet> {
                description = "Query type definition for paginated employee query."
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
                resolver { pageable: Pageable? ->
                    val page: Page<Employee> = service.findAll(pageable = pageable)
                    EmployeeSet(
                        content = page.content,
                        info = page.info
                    )
                }
            }

            query("filterEmployees") {
                description = "Filterable paginated Employee query."
                resolver { filterSet: EmployeeFilterSet, pageable: Pageable? ->
                    val page: Page<Employee> = service.filter(filterSet = filterSet, pageable = pageable)
                    EmployeeSet(
                        content = page.content,
                        info = page.info
                    )
                }
            }
        }

        return this
    }
}
