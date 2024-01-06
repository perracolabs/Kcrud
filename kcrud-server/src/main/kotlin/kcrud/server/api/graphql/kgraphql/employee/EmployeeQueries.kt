/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.api.graphql.kgraphql.employee

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import kcrud.base.api.graphql.frameworks.kgraphql.annotation.KGraphQLAPI
import kcrud.base.api.graphql.frameworks.kgraphql.context.SessionContext
import kcrud.base.api.graphql.frameworks.kgraphql.utils.GraphQLError
import kcrud.base.data.pagination.Page
import kcrud.base.data.pagination.Pageable
import kcrud.server.domain.entities.employee.Employee
import kcrud.server.domain.entities.employee.EmployeeConnection
import kcrud.server.domain.entities.employee.EmployeeFilterSet
import kcrud.server.domain.exceptions.EmployeeError
import kcrud.server.domain.services.EmployeeService
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
            type<EmployeeConnection> {
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
                resolver { employeeId: UUID ->
                    service.findById(employeeId = employeeId)
                        ?: GraphQLError.of(error = EmployeeError.EmployeeNotFound(employeeId = employeeId))
                }
            }

            query("employees") {
                description = "Returns all existing employees."
                resolver { context: Context, pageable: Pageable? ->

                    // Example of how to get the user from the context.
                    // See SessionContext for more details.
                    SessionContext(context = context).printUser()

                    val page: Page<Employee> = service.findAll(pageable = pageable)
                    EmployeeConnection(page = page)
                }
            }

            query("filterEmployees") {
                description = "Filterable paginated Employee query."
                resolver { filterSet: EmployeeFilterSet, pageable: Pageable? ->
                    val page: Page<Employee> = service.filter(filterSet = filterSet, pageable = pageable)
                    EmployeeConnection(page = page)
                }
            }
        }

        return this
    }
}
