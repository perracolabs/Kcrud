/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.exceptions

import io.ktor.http.*
import kcrud.base.exceptions.shared.BaseError
import kcrud.base.exceptions.shared.ErrorCodeRegistry
import kotlinx.datetime.LocalDate
import java.util.*

sealed class EmploymentError(
    status: HttpStatusCode,
    code: String,
    description: String
) : BaseError(status = status, code = code, description = description) {

    data class EmploymentNotFound(val employeeId: UUID, val employmentId: UUID) : EmploymentError(
        status = HttpStatusCode.NotFound,
        code = "${TAG}ENF",
        description = "Employment not found. Employee Id: $employeeId. Employment Id: $employmentId."
    )

    data class PeriodDatesMismatch(
        val employeeId: UUID,
        val employmentId: UUID?,
        val startDate: LocalDate,
        val endDate: LocalDate
    ) : EmploymentError(
        status = HttpStatusCode.BadRequest,
        code = "${TAG}PDM",
        description = "Employment end date cannot be prior to the start date. " +
                "Employee Id: $employeeId. Employment Id: $employmentId. " +
                "Start Date: $startDate. End Date: $endDate."
    )

    data class InvalidProbationEndDate(
        val employeeId: UUID,
        val employmentId: UUID?,
        val startDate: LocalDate,
        val probationEndDate: LocalDate,
    ) : EmploymentError(
        status = HttpStatusCode.BadRequest,
        code = "${TAG}IPD",
        description = "Employment probation end date cannot be prior to the start date. " +
                "Employee Id: $employeeId. Employment Id: $employmentId. " +
                "Start Date: $startDate. Probation End Date: $probationEndDate."
    )

    companion object {
        private const val TAG: String = "EMT."

        init {
            ErrorCodeRegistry.registerTag(tag = TAG)
        }
    }
}
