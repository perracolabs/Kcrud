/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.controllers.EmployeeController
import com.kcrud.data.repositories.EmployeeRepository
import com.kcrud.data.repositories.IEmployeeRepository
import com.kcrud.services.EmployeeService
import io.ktor.server.application.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

/**
 * Sets up and initializes dependency injection using the Koin framework.
 *
 * See: [Koin for Ktor Documentation](https://insert-koin.io/docs/quickstart/ktor)
 */
fun Application.configureDependencyInjection() {

    // Declare a Koin module to manage application-level dependencies.
    val appModule = module {

        // Provide a singleton instance of EmployeeRepository for any IEmployeeRepository requests.
        singleOf(::EmployeeRepository) { bind<IEmployeeRepository>() }

        // Instantiate a singleton EmployeeService, injecting the required EmployeeRepository.
        singleOf(::EmployeeService)

        // Instantiate a singleton EmployeeController, injecting the required EmployeeService.
        singleOf(::EmployeeController)
    }

    // Initialize Koin dependency injection with the defined module.
    install(plugin = Koin) {
        modules(appModule)
    }
}
