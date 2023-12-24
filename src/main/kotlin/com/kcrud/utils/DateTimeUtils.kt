/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.reflect.KProperty

/**
 * Delegate for calculating an Age based on a given date of birth (dob).
 */
internal class AgeDelegate(private val dob: LocalDate) {
    operator fun getValue(ignoreThisRef: Any?, ignoreProperty: KProperty<*>): Int {
        return DateTimeUtils.calculateAge(dob)
    }
}

/**
 * Singleton providing time-related utility functions.
 */
internal object DateTimeUtils {
    fun calculateAge(dob: LocalDate): Int {
        // Get today's date based on the system clock and timezone.
        val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

        // Calculate the difference in years.
        val age = currentDate.year - dob.year

        // Adjust the age if the birthday hasn't occurred this year yet.
        return if (compareBy<LocalDate> { it.year }.thenBy { it.monthNumber }.thenBy { it.dayOfMonth }
                .compare(dob, currentDate) > 0) age - 1 else age
    }
}
