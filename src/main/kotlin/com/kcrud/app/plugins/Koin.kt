package com.kcrud.app.plugins

import com.kcrud.controllers.EmployeeController
import com.kcrud.data.repositories.EmployeeRepository
import com.kcrud.data.repositories.IEmployeeRepository
import com.kcrud.services.EmployeeService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin


/**
 * Application extension function to configure dependency injection.
 *
 * See [Koin for Ktor documentation](https://insert-koin.io/docs/quickstart/ktor)
 */
fun Application.configureDependencyInjection() {

    // Define the Koin module for application dependencies.
    val appModule = module {

        // Bind EmployeeRepository to the IEmployeeRepository interface.
        // Create a single instance of EmployeeRepository when an IEmployeeRepository is requested.
        single<IEmployeeRepository> { EmployeeRepository() }

        // Create a single instance of EmployeeService.
        single { EmployeeService(get()) }

        // Create a single instance of EmployeeController.
        single { EmployeeController(get()) }
    }

    // Install the Koin DI framework and load the defined module.
    install(plugin = Koin) {
        modules(appModule)
    }
}


