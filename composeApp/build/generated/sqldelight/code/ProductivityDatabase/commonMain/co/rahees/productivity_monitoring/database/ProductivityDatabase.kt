package co.rahees.productivity_monitoring.database

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import co.rahees.productivity_monitoring.database.composeApp.newInstance
import co.rahees.productivity_monitoring.database.composeApp.schema
import co.rahees.productivitymonitoring.database.NoteQueries
import co.rahees.productivitymonitoring.database.PomodoroSessionQueries
import co.rahees.productivitymonitoring.database.TodoItemQueries
import kotlin.Unit

public interface ProductivityDatabase : Transacter {
  public val noteQueries: NoteQueries

  public val pomodoroSessionQueries: PomodoroSessionQueries

  public val todoItemQueries: TodoItemQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = ProductivityDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): ProductivityDatabase =
        ProductivityDatabase::class.newInstance(driver)
  }
}
