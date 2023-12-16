/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia.schema.employment

import com.expediagroup.graphql.server.operations.Mutation
import com.kcrud.data.models.employment.Employment
import com.kcrud.data.models.employment.EmploymentParams
import com.kcrud.graphql.expedia.ExpediaAPI
import com.kcrud.services.EmploymentService
import java.util.*

/**
 * Employment mutation definitions.
 *
 * @param service The service used in mutation resolvers.
 */
@Suppress("unused")
@ExpediaAPI
class EmploymentMutations(private val service: EmploymentService) : Mutation {

    fun createEmployment(employeeId: UUID, employment: EmploymentParams): Employment {
        return service.create(employeeId = employeeId, employment = employment)
    }

    fun updateEmployment(employeeId: UUID, employmentId: UUID, employment: EmploymentParams): Employment? {
        return service.update(employeeId = employeeId, employmentId = employmentId, employment = employment)
    }

    fun deleteEmployment(employmentId: UUID): Int {
        return service.delete(employmentId = employmentId)
    }

    fun deleteAllEmployments(employeeId: UUID): Int {
        return service.deleteAll(employeeId = employeeId)
    }
}
