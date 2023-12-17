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
import org.koin.mp.KoinPlatform.getKoin
import java.util.*

/**
 * Employment query definitions.
 */
@ExpediaAPI
class EmploymentQueries : Query {

    private val service: EmploymentService = getKoin().get()

    fun employment(employeeId: UUID, employmentId: UUID): Employment? {
        return service.findById(employeeId = employeeId, employmentId = employmentId)
    }

    fun employments(employeeId: UUID): List<Employment> {
        return service.findByEmployeeId(employeeId = employeeId)
    }
}
