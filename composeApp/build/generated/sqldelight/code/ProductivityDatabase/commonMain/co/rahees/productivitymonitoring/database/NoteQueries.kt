package co.rahees.productivitymonitoring.database

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.String

public class NoteQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    id: String,
    content: String,
    createdAt: String,
    lastModified: String,
  ) -> T): Query<T> = Query(670_146_632, arrayOf("Note"), driver, "Note.sq", "selectAll",
      "SELECT * FROM Note ORDER BY lastModified DESC") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!
    )
  }

  public fun selectAll(): Query<Note> = selectAll { id, content, createdAt, lastModified ->
    Note(
      id,
      content,
      createdAt,
      lastModified
    )
  }

  public fun <T : Any> selectById(id: String, mapper: (
    id: String,
    content: String,
    createdAt: String,
    lastModified: String,
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!
    )
  }

  public fun selectById(id: String): Query<Note> = selectById(id) { id_, content, createdAt,
      lastModified ->
    Note(
      id_,
      content,
      createdAt,
      lastModified
    )
  }

  public fun insert(
    id: String,
    content: String,
    createdAt: String,
    lastModified: String,
  ) {
    driver.execute(-1_544_569_770, """
        |INSERT INTO Note (id, content, createdAt, lastModified)
        |VALUES (?, ?, ?, ?)
        """.trimMargin(), 4) {
          bindString(0, id)
          bindString(1, content)
          bindString(2, createdAt)
          bindString(3, lastModified)
        }
    notifyQueries(-1_544_569_770) { emit ->
      emit("Note")
    }
  }

  public fun updateContent(
    content: String,
    lastModified: String,
    id: String,
  ) {
    driver.execute(-2_062_795_981, """UPDATE Note SET content = ?, lastModified = ? WHERE id = ?""",
        3) {
          bindString(0, content)
          bindString(1, lastModified)
          bindString(2, id)
        }
    notifyQueries(-2_062_795_981) { emit ->
      emit("Note")
    }
  }

  public fun deleteById(id: String) {
    driver.execute(-1_574_671_558, """DELETE FROM Note WHERE id = ?""", 1) {
          bindString(0, id)
        }
    notifyQueries(-1_574_671_558) { emit ->
      emit("Note")
    }
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Note", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Note", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-700_249_589, """SELECT * FROM Note WHERE id = ?""", mapper, 1) {
      bindString(0, id)
    }

    override fun toString(): String = "Note.sq:selectById"
  }
}
