/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.api.graphql.kgraphql.employment

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import kcrud.base.api.graphql.frameworks.kgraphql.annotation.KGraphQLAPI
import kcrud.server.domain.entities.employment.EmploymentRequest
import kcrud.server.domain.services.EmploymentService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

/**
 * Employment mutation definitions.
 *
 * @param schemaBuilder The SchemaBuilder instance for configuring the schema.
 */
@KGraphQLAPI
internal class EmploymentMutations(private val schemaBuilder: SchemaBuilder) : KoinComponent {

    private val service: EmploymentService by inject()

    /**
     * Configures input types for mutations.
     */
    fun configureInputs(): EmploymentMutations {
        schemaBuilder.apply {
            inputType<EmploymentRequest> {
                name = "Input type definition for Employments."
            }
        }

        return this
    }

    fun configureMutations(): EmploymentMutations {
        schemaBuilder.apply {
            mutation("createEmployment") {
                description = "Creates a new employment."
                resolver { employeeId: UUID, employment: EmploymentRequest ->
                    service.create(employeeId = employeeId, employmentRequest = employment)
                }
            }

            mutation("updateEmployment") {
                description = "Updates an existing employment."
                resolver { employeeId: UUID, employmentId: UUID, employment: EmploymentRequest ->
                    service.update(
                        employeeId = employeeId,
                        employmentId = employmentId,
                        employmentRequest = employment
                    )
                }
            }

            mutation("deleteEmployment") {
                description = "Deletes an existing employment."
                resolver { employmentId: UUID ->
                    service.delete(employmentId = employmentId)
                }
            }

            mutation("deleteAllEmployments") {
                description = "Deletes all employments for an existing employee."
                resolver { employeeId: UUID ->
                    service.deleteAll(employeeId = employeeId)
                }
            }
        }

        return this
    }
}
