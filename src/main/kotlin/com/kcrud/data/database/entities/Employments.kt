/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date

/**
 * Database entity for employments.
 * An employee may have multiple employments, which indicates re-hiring.
 */
internal object Employments : Table(name ="employments") {
    val id = integer(name = "employment_id").autoIncrement()
    val employeeId = integer(name = "employee_id") references Employees.id
    val probationEndDate = date(name = "probation_end_date").nullable()
    val isActive = bool(name = "is_active")
    val startDate = date(name = "start_date")
    val endDate = date(name = "end_date").nullable()
    val comments = varchar(name = "comments", length = 256).nullable()

    override val primaryKey = PrimaryKey(firstColumn = id, name = "PK_Employment_ID")
}
