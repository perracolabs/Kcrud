/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.api.graphql.expedia.employment

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import graphql.execution.DataFetcherResult
import kcrud.base.api.graphql.frameworks.expedia.annotation.ExpediaAPI
import kcrud.base.api.graphql.frameworks.expedia.utils.GraphQLResult
import kcrud.base.exceptions.shared.KcrudException
import kcrud.server.domain.entities.employment.Employment
import kcrud.server.domain.entities.employment.EmploymentRequest
import kcrud.server.domain.exceptions.EmploymentError
import kcrud.server.domain.services.EmploymentService
import org.koin.mp.KoinPlatform.getKoin
import java.util.*

/**
 * Employment mutation definitions.
 */
@Suppress("unused", "RedundantSuspendModifier")
@ExpediaAPI
class EmploymentMutations : Mutation {

    private val service: EmploymentService = getKoin().get()

    @GraphQLDescription("Creates a new employment.")
    suspend fun createEmployment(employeeId: UUID, employment: EmploymentRequest): DataFetcherResult<Employment?> {
        try {
            val newEmployment: Employment = service.create(employeeId = employeeId, employmentRequest = employment)
            return GraphQLResult.of(data = newEmployment, error = null)
        } catch (e: KcrudException) {
            return GraphQLResult.of(data = null, error = e.error)
        }
    }

    @GraphQLDescription("Updates an existing employment.")
    suspend fun updateEmployment(employeeId: UUID, employmentId: UUID, employment: EmploymentRequest): DataFetcherResult<Employment?> {
        val updatedEmployment: Employment?

        try {
            updatedEmployment = service.update(
                employeeId = employeeId,
                employmentId = employmentId,
                employmentRequest = employment
            )
        } catch (e: KcrudException) {
            return GraphQLResult.of(data = null, error = e.error)
        }

        val error = if (updatedEmployment == null)
            EmploymentError.EmploymentNotFound(employeeId = employeeId, employmentId = employmentId)
        else
            null

        return GraphQLResult.of(data = updatedEmployment, error = error)
    }

    @GraphQLDescription("Deletes an existing employment.")
    suspend fun deleteEmployment(employmentId: UUID): Int {
        return service.delete(employmentId = employmentId)
    }

    @GraphQLDescription("Deletes all employments for a given employee.")
    suspend fun deleteAllEmployments(employeeId: UUID): Int {
        return service.deleteAll(employeeId = employeeId)
    }
}
