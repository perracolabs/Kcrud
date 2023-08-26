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


fun Application.configureDependencyInjection() {
    val appModule = module {
        singleOf(::EmployeeRepository) { bind<IEmployeeRepository>() }
        singleOf(::EmployeeService)
        singleOf(::EmployeeController)
    }

    install(plugin = Koin) {
        modules(appModule)
    }
}



