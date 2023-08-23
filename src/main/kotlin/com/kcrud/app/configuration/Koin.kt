package com.kcrud.app.configuration

import com.kcrud.controllers.UserController
import com.kcrud.data.repositories.IUserRepository
import com.kcrud.data.repositories.UserRepository
import com.kcrud.services.UserService
import io.ktor.server.application.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.core.module.dsl.bind


fun Application.configureDependencyInjection() {
    val appModule = module {
        singleOf(::UserRepository) { bind<IUserRepository>() }
        singleOf(::UserService)
        singleOf(::UserController)
    }

    install(plugin = Koin) {
        modules(appModule)
    }
}



