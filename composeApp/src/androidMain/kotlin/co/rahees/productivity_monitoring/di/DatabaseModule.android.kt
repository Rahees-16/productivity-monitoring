package co.rahees.productivity_monitoring.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import co.rahees.productivity_monitoring.database.ProductivityDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val databaseModule = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = ProductivityDatabase.Schema,
            context = androidContext(),
            name = "productivity.db"
        )
    }
    
    single<ProductivityDatabase> {
        ProductivityDatabase(get())
    }
}