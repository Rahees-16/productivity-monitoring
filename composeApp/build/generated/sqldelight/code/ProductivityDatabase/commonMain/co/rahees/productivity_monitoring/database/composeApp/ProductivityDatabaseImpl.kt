package co.rahees.productivity_monitoring.database.composeApp

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import co.rahees.productivity_monitoring.database.ProductivityDatabase
import co.rahees.productivitymonitoring.database.NoteQueries
import co.rahees.productivitymonitoring.database.PomodoroSessionQueries
import co.rahees.productivitymonitoring.database.TodoItemQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<ProductivityDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = ProductivityDatabaseImpl.Schema

internal fun KClass<ProductivityDatabase>.newInstance(driver: SqlDriver): ProductivityDatabase =
    ProductivityDatabaseImpl(driver)

private class ProductivityDatabaseImpl(
  driver: SqlDriver,
) : TransacterImpl(driver), ProductivityDatabase {
  override val noteQueries: NoteQueries = NoteQueries(driver)

  override val pomodoroSessionQueries: PomodoroSessionQueries = PomodoroSessionQueries(driver)

  override val todoItemQueries: TodoItemQueries = TodoItemQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE Note (
          |    id TEXT NOT NULL PRIMARY KEY,
          |    content TEXT NOT NULL,
          |    createdAt TEXT NOT NULL,
          |    lastModified TEXT NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE PomodoroSession (
          |    id TEXT NOT NULL PRIMARY KEY,
          |    startTime TEXT NOT NULL,
          |    endTime TEXT DEFAULT NULL,
          |    duration INTEGER NOT NULL,
          |    isCompleted INTEGER NOT NULL DEFAULT 0,
          |    sessionType TEXT NOT NULL DEFAULT 'WORK'
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE TodoItem (
          |    id TEXT NOT NULL PRIMARY KEY,
          |    title TEXT NOT NULL,
          |    description TEXT NOT NULL DEFAULT '',
          |    isCompleted INTEGER NOT NULL DEFAULT 0,
          |    createdAt TEXT NOT NULL,
          |    completedAt TEXT DEFAULT NULL
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Unit
  }
}
