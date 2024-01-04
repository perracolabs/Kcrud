/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.api.graphql.frameworks.kgraphql.schema.employment

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.core.api.graphql.frameworks.kgraphql.annotation.KGraphQLAPI
import com.kcrud.core.domain.entities.employment.Employment
import com.kcrud.core.domain.services.EmploymentService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

/**
 * Employment query definitions.
 *
 * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
 */
@KGraphQLAPI
internal class EmploymentQueries(private val schemaBuilder: SchemaBuilder) : KoinComponent {

    private val service: EmploymentService by inject()

    /**
     * Configures query types specifically.
     */
    fun configureTypes(): EmploymentQueries {
        schemaBuilder.apply {
            type<Employment> {
                description = "Query type definition for employments."
            }
        }

        return this
    }

    /**
     * Configures query resolvers to fetch data.
     */
    fun configureQueries(): EmploymentQueries {
        schemaBuilder.apply {
            query("employment") {
                description = "Returns a single employment given its id."
                resolver { employeeId: UUID, employmentId: UUID ->
                    service.findById(
                        employeeId = employeeId,
                        employmentId = employmentId
                    )
                }
            }
            query("employments") {
                description = "Returns all employments for a given employee."
                resolver { employeeId: UUID -> service.findByEmployeeId(employeeId = employeeId) }
            }
        }

        return this
    }
}
