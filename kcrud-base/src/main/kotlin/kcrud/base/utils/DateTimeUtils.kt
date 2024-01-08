/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.utils

import kotlinx.datetime.*
import kotlin.reflect.KProperty

/**
 * Delegate for calculating an Age based on a given date of birth (dob).
 */
class AgeDelegate(private val dob: LocalDate) {
    operator fun getValue(ignoreThisRef: Any?, ignoreProperty: KProperty<*>): Int {
        return DateTimeUtils.calculateAge(dob = dob)
    }
}

/**
 * Singleton providing time-related utility functions.
 */
object DateTimeUtils {
    fun calculateAge(dob: LocalDate): Int {
        // Get today's date based on the system clock and timezone.
        val currentDate: LocalDate = currentUTCDate()

        // Calculate the difference in years.
        val age: Int = currentDate.year - dob.year

        val birthdayAlreadyPassed: Boolean = (dob.monthNumber < currentDate.monthNumber) ||
                (dob.monthNumber == currentDate.monthNumber && dob.dayOfMonth <= currentDate.dayOfMonth)

        // Adjust the age if the birthday hasn't occurred this year yet.
        return age.takeIf { birthdayAlreadyPassed } ?: (age - 1)
    }

    /**
     * Returns the current date-time in UTC.
     */
    fun currentUTCDateTime(): LocalDateTime = Clock.System.now().toLocalDateTime(timeZone = TimeZone.UTC)

    /**
     * Returns the current date in UTC.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun currentUTCDate(): LocalDate = Clock.System.todayIn(timeZone = TimeZone.currentSystemDefault())

    /**
     * Converts a UTC time to the local time zone.
     */
    fun utcToLocal(utc: LocalDateTime): LocalDateTime {
        return utc.toInstant(timeZone = TimeZone.UTC).toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
    }
}
