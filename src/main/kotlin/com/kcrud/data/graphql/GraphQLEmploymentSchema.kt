/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.graphql

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.data.models.Employment
import com.kcrud.services.EmploymentService
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
class GraphQLEmploymentSchema(private val schemaBuilder: SchemaBuilder, private val service: EmploymentService) {

    /**
     * Configures query types specifically.
     */
    fun configureQueryTypes(): GraphQLEmploymentSchema {
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
    fun configureQueries(): GraphQLEmploymentSchema {
        schemaBuilder.apply {
            query("employment") {
                description = "Returns a single employment given its id."
                resolver { employmentId: String -> service.findById(employmentId = employmentId.toUUID()) }
            }
            query("employments") {
                description = "Returns all employments for a given employee."
                resolver { employeeId: String -> service.findByEmployeeId(employeeId = employeeId.toUUID()) }
            }
        }

        return this
    }

    /**
     * Configures input types for mutations.
     */
    fun configureMutationInputs(): GraphQLEmploymentSchema {
        schemaBuilder.apply {
            inputType<Employment> {
                name = "Input type definition for Employments."
            }
        }

        return this
    }

    fun configureMutations(): GraphQLEmploymentSchema {
        schemaBuilder.apply {
            mutation("createEmployment") {
                description = "Creates a new employment."
                resolver { employeeId: String, employment: Employment ->
                    service.create(employeeId = employeeId.toUUID(), employment = employment)
                }
            }

            mutation("updateEmployment") {
                description = "Updates an existing employment."
                resolver { employeeId: String, employmentId: String, employment: Employment ->
                    service.update(
                        employeeId = employeeId.toUUID(),
                        employmentId = employmentId.toUUID(),
                        employment = employment
                    )
                }
            }

            mutation("deleteEmployment") {
                description = "Deletes an existing employment."
                resolver { employmentId: String -> service.delete(employmentId = employmentId.toUUID()) }
            }
        }

        return this
    }
}
