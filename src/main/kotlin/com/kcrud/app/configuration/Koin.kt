package com.kcrud.app.configuration

import com.kcrud.controllers.EmployeeController
import com.kcrud.data.repositories.IEmployeeRepository
import com.kcrud.data.repositories.EmployeeRepository
import com.kcrud.services.EmployeeService
import io.ktor.server.application.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.core.module.dsl.bind


/**
 *  Extension function to configure dependency injection for a Ktor Application using Koin.
 */
fun Application.configureDependencyInjection() {

    // Define the Koin module for application dependencies.
    val appModule = module {

        // Bind EmployeeRepository to the IEmployeeRepository interface.
        // Create a single instance of EmployeeRepository when an IEmployeeRepository is requested.
        singleOf(::EmployeeRepository) { bind<IEmployeeRepository>() }

        // Create a single instance of EmployeeService.
        singleOf(::EmployeeService)

        // Create a single instance of EmployeeController.
        singleOf(::EmployeeController)
    }

    // Install the Koin DI framework and load the defined module.
    install(plugin = Koin) {
        modules(appModule)
    }
}


