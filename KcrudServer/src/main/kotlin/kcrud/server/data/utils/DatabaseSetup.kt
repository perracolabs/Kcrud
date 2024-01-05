/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.data.utils

import kcrud.core.data.database.database.DatabaseManager
import kcrud.server.data.tables.ContactTable
import kcrud.server.data.tables.EmployeeTable
import kcrud.server.data.tables.EmploymentTable

internal object DatabaseSetup {

    fun configure() {
        DatabaseManager.init {
            addTable(ContactTable)
            addTable(EmployeeTable)
            addTable(EmploymentTable)
        }
    }
}