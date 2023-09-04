package com.kcrud.plugins

import com.kcrud.controllers.EmployeeController
import com.kcrud.data.repositories.EmployeeRepository
import com.kcrud.data.repositories.IEmployeeRepository
import com.kcrud.services.EmployeeService
import io.ktor.server.application.*
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
        single<IEmployeeRepository> { EmployeeRepository() }

        // Instantiate a singleton EmployeeService, injecting the required EmployeeRepository.
        single { EmployeeService(get()) }

        // Instantiate a singleton EmployeeController, injecting the required EmployeeService.
        single { EmployeeController(get()) }
    }

    // Initialize Koin dependency injection with the defined module.
    install(plugin = Koin) {
        modules(appModule)
    }
}
