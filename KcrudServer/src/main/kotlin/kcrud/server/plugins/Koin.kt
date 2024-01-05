/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.plugins

import io.ktor.server.application.*
import kcrud.server.domain.repositories.contact.ContactRepository
import kcrud.server.domain.repositories.contact.IContactRepository
import kcrud.server.domain.repositories.employee.EmployeeRepository
import kcrud.server.domain.repositories.employee.IEmployeeRepository
import kcrud.server.domain.repositories.employment.EmploymentRepository
import kcrud.server.domain.repositories.employment.IEmploymentRepository
import kcrud.server.domain.services.EmployeeService
import kcrud.server.domain.services.EmploymentService
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

/**
 * Sets up and initializes dependency injection using the Koin framework.
 *
 * See: [Koin for Ktor Documentation](https://insert-koin.io/docs/quickstart/ktor)
 */
fun Application.configureKoin() {

    // Declare a Koin module to manage application-level dependencies.
    val moduleList = module {
        single<IContactRepository> { ContactRepository() }

        single<IEmployeeRepository> { EmployeeRepository(get()) }
        single { EmployeeService(get()) }

        single<IEmploymentRepository> { EmploymentRepository() }
        single { EmploymentService(get()) }
    }

    // Initialize Koin dependency injection with the defined module.
    install(Koin) {
        modules(moduleList)
    }
}
