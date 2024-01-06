/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.data.tables

import kcrud.base.data.utils.enumById
import kcrud.server.domain.entities.employment.types.WorkModality
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date

/**
 * Database table definition for employments.
 * An employee may have multiple employments, which indicates re-hiring.
 */
internal object EmploymentTable : Table(name = "employment") {
    val id = uuid(name = "employment_id").autoGenerate()
    val employeeId = uuid(name = "employee_id").references(ref = EmployeeTable.id, onDelete = ReferenceOption.CASCADE)
    val probationEndDate = date(name = "probation_end_date").nullable()
    val workModality = enumById(name = "work_modality", fromId = WorkModality::fromId)
    val isActive = bool(name = "is_active")
    val startDate = date(name = "start_date")
    val endDate = date(name = "end_date").nullable()
    val comments = varchar(name = "comments", length = 256).nullable()

    override val primaryKey = PrimaryKey(firstColumn = id, name = "PK_Employment_ID")
}
