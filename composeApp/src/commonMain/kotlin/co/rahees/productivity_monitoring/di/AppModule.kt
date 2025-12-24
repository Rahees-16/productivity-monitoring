package co.rahees.productivity_monitoring.di

import co.rahees.productivity_monitoring.data.repositories.TodoRepositoryImpl
import co.rahees.productivity_monitoring.domain.repositories.TodoRepository
import co.rahees.productivity_monitoring.domain.usecases.TodoUseCases
import co.rahees.productivity_monitoring.presentation.viewmodels.TodoViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // Repositories
    singleOf(::TodoRepositoryImpl) bind TodoRepository::class
    
    // Use Cases
    factoryOf(::TodoUseCases)
    
    // ViewModels
    factoryOf(::TodoViewModel)
}