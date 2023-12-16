/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.kgraphql.schema.employment

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.kcrud.data.models.employment.EmploymentParams
import com.kcrud.graphql.kgraphql.KGraphQLAPI
import com.kcrud.services.EmploymentService
import java.util.*


/**
 * Employment mutation definitions.
 *
 * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
 * @param service The service used in mutation resolvers.
 */
@KGraphQLAPI
internal class EmploymentMutations(private val schemaBuilder: SchemaBuilder, private val service: EmploymentService) {

    /**
     * Configures input types for mutations.
     */
    fun configureInputs(): EmploymentMutations {
        schemaBuilder.apply {
            inputType<EmploymentParams> {
                name = "Input type definition for Employments."
            }
        }

        return this
    }

    fun configureMutations(): EmploymentMutations {
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

            mutation("deleteAllEmployments") {
                description = "Deletes all employments for an existing employee."
                resolver { employeeId: UUID -> service.deleteAll(employeeId = employeeId) }
            }
        }

        return this
    }
}