/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.api.graphql.expedia.employment

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import graphql.execution.DataFetcherResult
import kcrud.base.api.graphql.frameworks.expedia.annotation.ExpediaAPI
import kcrud.base.api.graphql.frameworks.expedia.utils.GraphQLResult
import kcrud.server.domain.entities.employment.Employment
import kcrud.server.domain.exceptions.EmploymentError
import kcrud.server.domain.services.EmploymentService
import org.koin.mp.KoinPlatform.getKoin
import java.util.*

/**
 * Employment query definitions.
 */
@ExpediaAPI
class EmploymentQueries : Query {

    private val service: EmploymentService = getKoin().get()

    @GraphQLDescription("Returns a concrete employment for a given employee.")
    fun employment(employeeId: UUID, employmentId: UUID): DataFetcherResult<Employment?> {
        val employment: Employment? = service.findById(employeeId = employeeId, employmentId = employmentId)

        val error = if (employment == null)
            EmploymentError.EmploymentNotFound(employeeId = employeeId, employmentId = employmentId)
        else
            null

        return GraphQLResult.of(data = employment, error = error)
    }

    @GraphQLDescription("Returns all employments for a given employee.")
    fun employments(employeeId: UUID): List<Employment> {
        return service.findByEmployeeId(employeeId = employeeId)
    }
}
