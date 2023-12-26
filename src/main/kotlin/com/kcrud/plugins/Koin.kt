/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.domain.repositories.contact.ContactRepository
import com.kcrud.domain.repositories.contact.IContactRepository
import com.kcrud.domain.repositories.employee.EmployeeRepository
import com.kcrud.domain.repositories.employee.IEmployeeRepository
import com.kcrud.domain.repositories.employment.EmploymentRepository
import com.kcrud.domain.repositories.employment.IEmploymentRepository
import com.kcrud.domain.services.EmployeeService
import com.kcrud.domain.services.EmploymentService
import io.ktor.server.application.*
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
