/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models.employment

import com.kcrud.data.models.employment.types.WorkModality
import com.kcrud.data.models.shared.Period
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * Represents the request to create/update an employment.
 *
 * @property period The employment's period details.
 * @property probationEndDate Optional employment's probation end date.
 */
@Serializable
data class EmploymentParams(
    val period: Period,
    val probationEndDate: LocalDate? = null,
    val workModality: WorkModality
) {
    init {
        probationEndDate?.let { date ->
            require(date >= period.startDate) {
                "Employment probation end date cannot be earlier than period start date."
            }
        }
    }
}
