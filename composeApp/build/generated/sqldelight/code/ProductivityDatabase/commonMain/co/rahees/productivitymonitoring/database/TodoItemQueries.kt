package co.rahees.productivitymonitoring.database

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class TodoItemQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    id: String,
    title: String,
    description: String,
    isCompleted: Long,
    createdAt: String,
    completedAt: String?,
  ) -> T): Query<T> = Query(-332_712_543, arrayOf("TodoItem"), driver, "TodoItem.sq", "selectAll",
      "SELECT * FROM TodoItem ORDER BY createdAt DESC") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)
    )
  }

  public fun selectAll(): Query<TodoItem> = selectAll { id, title, description, isCompleted,
      createdAt, completedAt ->
    TodoItem(
      id,
      title,
      description,
      isCompleted,
      createdAt,
      completedAt
    )
  }

  public fun <T : Any> selectById(id: String, mapper: (
    id: String,
    title: String,
    description: String,
    isCompleted: Long,
    createdAt: String,
    completedAt: String?,
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)
    )
  }

  public fun selectById(id: String): Query<TodoItem> = selectById(id) { id_, title, description,
      isCompleted, createdAt, completedAt ->
    TodoItem(
      id_,
      title,
      description,
      isCompleted,
      createdAt,
      completedAt
    )
  }

  public fun insert(
    id: String,
    title: String,
    description: String,
    isCompleted: Long,
    createdAt: String,
    completedAt: String?,
  ) {
    driver.execute(1_542_507_933, """
        |INSERT INTO TodoItem (id, title, description, isCompleted, createdAt, completedAt)
        |VALUES (?, ?, ?, ?, ?, ?)
        """.trimMargin(), 6) {
          bindString(0, id)
          bindString(1, title)
          bindString(2, description)
          bindLong(3, isCompleted)
          bindString(4, createdAt)
          bindString(5, completedAt)
        }
    notifyQueries(1_542_507_933) { emit ->
      emit("TodoItem")
    }
  }

  public fun updateCompletion(
    isCompleted: Long,
    completedAt: String?,
    id: String,
  ) {
    driver.execute(-1_603_989_655,
        """UPDATE TodoItem SET isCompleted = ?, completedAt = ? WHERE id = ?""", 3) {
          bindLong(0, isCompleted)
          bindString(1, completedAt)
          bindString(2, id)
        }
    notifyQueries(-1_603_989_655) { emit ->
      emit("TodoItem")
    }
  }

  public fun updateTitle(title: String, id: String) {
    driver.execute(1_614_361_259, """UPDATE TodoItem SET title = ? WHERE id = ?""", 2) {
          bindString(0, title)
          bindString(1, id)
        }
    notifyQueries(1_614_361_259) { emit ->
      emit("TodoItem")
    }
  }

  public fun updateDescription(description: String, id: String) {
    driver.execute(-311_178_737, """UPDATE TodoItem SET description = ? WHERE id = ?""", 2) {
          bindString(0, description)
          bindString(1, id)
        }
    notifyQueries(-311_178_737) { emit ->
      emit("TodoItem")
    }
  }

  public fun deleteById(id: String) {
    driver.execute(1_696_432_385, """DELETE FROM TodoItem WHERE id = ?""", 1) {
          bindString(0, id)
        }
    notifyQueries(1_696_432_385) { emit ->
      emit("TodoItem")
    }
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("TodoItem", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("TodoItem", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-1_724_112_942, """SELECT * FROM TodoItem WHERE id = ?""", mapper, 1) {
      bindString(0, id)
    }

    override fun toString(): String = "TodoItem.sq:selectById"
  }
}
