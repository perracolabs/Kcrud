/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.kgraphql.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.data.models.employment.Employment
import com.kcrud.data.models.employment.EmploymentParams
import com.kcrud.graphql.kgraphql.KGraphQLAPI
import com.kcrud.services.EmploymentService
import java.util.*


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
@KGraphQLAPI
internal class EmploymentSchema(private val schemaBuilder: SchemaBuilder, private val service: EmploymentService) {

    /**
     * Configures query types specifically.
     */
    fun configureQueryTypes(): EmploymentSchema {
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
    fun configureQueries(): EmploymentSchema {
        schemaBuilder.apply {
            query("employment") {
                description = "Returns a single employment given its id."
                resolver { employmentId: UUID -> service.findById(employmentId = employmentId) }
            }
            query("employments") {
                description = "Returns all employments for a given employee."
                resolver { employeeId: UUID -> service.findByEmployeeId(employeeId = employeeId) }
            }
        }

        return this
    }

    /**
     * Configures input types for mutations.
     */
    fun configureMutationInputs(): EmploymentSchema {
        schemaBuilder.apply {
            inputType<EmploymentParams> {
                name = "Input type definition for Employments."
            }
        }

        return this
    }

    fun configureMutations(): EmploymentSchema {
        schemaBuilder.apply {
            mutation("createEmployment") {
                description = "Creates a new employment."
                resolver { employeeId: UUID, employment: EmploymentParams ->
                    service.create(employeeId = employeeId, employment = employment)
                }
            }

            mutation("updateEmployment") {
                description = "Updates an existing employment."
                resolver { employeeId: UUID, employmentId: UUID, employment: EmploymentParams ->
                    service.update(
                        employeeId = employeeId,
                        employmentId = employmentId,
                        employment = employment
                    )
                }
            }

            mutation("deleteEmployment") {
                description = "Deletes an existing employment."
                resolver { employmentId: UUID -> service.delete(employmentId = employmentId) }
            }
        }

        return this
    }
}
