/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models.employment

import com.kcrud.data.models.shared.Period
import kotlinx.datetime.LocalDate

/**
 * This class represents the request to create an employment.
 *
 * @property period The employment's period.
 * @property probationEndDate The employment's probation end date.
 */
data class EmploymentRequest(
    val period: Period,
    val probationEndDate: LocalDate? = null,
) {
    init {
        probationEndDate?.let { date ->
            require(date >= period.startDate) {
                "Employment probation end date cannot be earlier than period start date."
            }
        }
    }
}
