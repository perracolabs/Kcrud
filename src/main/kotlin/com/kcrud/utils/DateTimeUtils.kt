package com.kcrud.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.reflect.KProperty

class AgeDelegate(private val dob: LocalDate) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return DateTimeUtils.calculateAge(dob)
    }
}

object DateTimeUtils {
    fun calculateAge(dob: LocalDate): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val age = today.year - dob.year
        return if (compareBy<LocalDate> { it.year }.thenBy { it.monthNumber }.thenBy { it.dayOfMonth }
                .compare(dob, today) > 0) age - 1 else age
    }
}
