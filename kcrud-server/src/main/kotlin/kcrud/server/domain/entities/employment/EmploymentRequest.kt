/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.entities.employment

import kcrud.server.domain.entities.employment.types.WorkModality
import kcrud.server.domain.entities.shared.Period
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * Represents the request to create/update an employment.
 *
 * @property period The employment's period details.
 * @property probationEndDate Optional employment's probation end date.
 */
@Serializable
data class EmploymentRequest(
    val period: Period,
    val probationEndDate: LocalDate? = null,
    val workModality: WorkModality
) {
    // Example of a validation rule within a data class.
    // This is not a good practice, as it couples the data class with the validation logic.
    //
    //    init {
    //        probationEndDate?.let { date ->
    //            require(date >= period.startDate) {
    //                "Employment probation end date cannot be earlier than period start date."
    //            }
    //        }
    //    }
    //
    // In addition to that, it is not possible to pass to the client more detailed information
    // about the error, such as the field name or concrete code for the error.
    // For a better approach, see the validation use of 'EmploymentError.PeriodDatesMismatch'.
}
