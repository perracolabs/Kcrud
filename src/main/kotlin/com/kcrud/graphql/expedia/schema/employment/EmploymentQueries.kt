/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.graphql.expedia.schema.employment

import com.expediagroup.graphql.server.operations.Query
import com.kcrud.data.models.employment.Employment
import com.kcrud.graphql.expedia.ExpediaAPI
import com.kcrud.services.EmploymentService
import java.util.*

/**
 * Employment query definitions.
 *
 * @param service The service used in query resolvers.
 */
@ExpediaAPI
class EmploymentQueries(private val service: EmploymentService) : Query {

    fun employment(employmentId: UUID): Employment? {
        return service.findById(employmentId = employmentId)
    }

    fun employments(employeeId: UUID): List<Employment> {
        return service.findByEmployeeId(employeeId = employeeId)
    }
}
