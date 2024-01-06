/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.data.utils

import kcrud.base.admin.settings.AppSettings
import kcrud.base.data.database.DatabaseFactory
import kcrud.server.data.tables.ContactTable
import kcrud.server.data.tables.EmployeeTable
import kcrud.server.data.tables.EmploymentTable

internal object DatabaseSetup {

    fun configure() {
        DatabaseFactory.init(settings = AppSettings.database) {
            addTable(ContactTable)
            addTable(EmployeeTable)
            addTable(EmploymentTable)
        }
    }
}
