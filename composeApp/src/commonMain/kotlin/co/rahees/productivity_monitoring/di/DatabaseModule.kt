package co.rahees.productivity_monitoring.di

import co.rahees.productivity_monitoring.database.ProductivityDatabase
import org.koin.dsl.module

expect val databaseModule: org.koin.core.module.Module

val commonDatabaseModule = module {
    // Common database dependencies can go here
}