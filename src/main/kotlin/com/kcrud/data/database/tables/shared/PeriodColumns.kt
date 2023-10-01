/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.tables.shared

import com.kcrud.data.database.tables.Contacts.nullable
import com.kcrud.data.models.shared.Period
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.statements.UpdateBuilder

/**
 * Container for period column definitions that are shared among multiple tables.
 *
 * This class must be used via composition over inheritance to allow tables to reuse common
 * column definitions without inheriting from a base table, which can cause issues like duplicate columns.
 *
 * @param table The Exposed Table to which the columns will be added.
 */
internal open class PeriodColumns(table: Table) {
    val isActive = table.bool(name = "is_active")
    val startDate = table.date(name = "start_date")
    val endDate = table.date(name = "end_date").nullable()
    val comments = table.varchar(name = "comments", length = 256).nullable()

    companion object {
        fun fromPeriodModel(target: UpdateBuilder<Int>, period: Period, columns: PeriodColumns) {
            target.apply {
                this[columns.isActive] = period.isActive
                this[columns.startDate] = period.startDate
                this[columns.endDate] = period.endDate
                this[columns.comments] = period.comments?.trim()
            }
        }
    }
}
