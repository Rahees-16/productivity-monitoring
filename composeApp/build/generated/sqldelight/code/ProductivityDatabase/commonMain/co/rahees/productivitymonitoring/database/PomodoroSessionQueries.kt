package co.rahees.productivitymonitoring.database

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class PomodoroSessionQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    id: String,
    startTime: String,
    endTime: String?,
    duration: Long,
    isCompleted: Long,
    sessionType: String,
  ) -> T): Query<T> = Query(-1_481_873_529, arrayOf("PomodoroSession"), driver,
      "PomodoroSession.sq", "selectAll", "SELECT * FROM PomodoroSession ORDER BY startTime DESC") {
      cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getLong(3)!!,
      cursor.getLong(4)!!,
      cursor.getString(5)!!
    )
  }

  public fun selectAll(): Query<PomodoroSession> = selectAll { id, startTime, endTime, duration,
      isCompleted, sessionType ->
    PomodoroSession(
      id,
      startTime,
      endTime,
      duration,
      isCompleted,
      sessionType
    )
  }

  public fun <T : Any> selectById(id: String, mapper: (
    id: String,
    startTime: String,
    endTime: String?,
    duration: Long,
    isCompleted: Long,
    sessionType: String,
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getLong(3)!!,
      cursor.getLong(4)!!,
      cursor.getString(5)!!
    )
  }

  public fun selectById(id: String): Query<PomodoroSession> = selectById(id) { id_, startTime,
      endTime, duration, isCompleted, sessionType ->
    PomodoroSession(
      id_,
      startTime,
      endTime,
      duration,
      isCompleted,
      sessionType
    )
  }

  public fun <T : Any> selectByDate(`value`: String, mapper: (
    id: String,
    startTime: String,
    endTime: String?,
    duration: Long,
    isCompleted: Long,
    sessionType: String,
  ) -> T): Query<T> = SelectByDateQuery(value) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getLong(3)!!,
      cursor.getLong(4)!!,
      cursor.getString(5)!!
    )
  }

  public fun selectByDate(value_: String): Query<PomodoroSession> = selectByDate(value_) { id,
      startTime, endTime, duration, isCompleted, sessionType ->
    PomodoroSession(
      id,
      startTime,
      endTime,
      duration,
      isCompleted,
      sessionType
    )
  }

  public fun insert(
    id: String,
    startTime: String,
    endTime: String?,
    duration: Long,
    isCompleted: Long,
    sessionType: String,
  ) {
    driver.execute(-1_238_280_841, """
        |INSERT INTO PomodoroSession (id, startTime, endTime, duration, isCompleted, sessionType)
        |VALUES (?, ?, ?, ?, ?, ?)
        """.trimMargin(), 6) {
          bindString(0, id)
          bindString(1, startTime)
          bindString(2, endTime)
          bindLong(3, duration)
          bindLong(4, isCompleted)
          bindString(5, sessionType)
        }
    notifyQueries(-1_238_280_841) { emit ->
      emit("PomodoroSession")
    }
  }

  public fun updateCompletion(
    isCompleted: Long,
    endTime: String?,
    id: String,
  ) {
    driver.execute(-1_884_746_045,
        """UPDATE PomodoroSession SET isCompleted = ?, endTime = ? WHERE id = ?""", 3) {
          bindLong(0, isCompleted)
          bindString(1, endTime)
          bindString(2, id)
        }
    notifyQueries(-1_884_746_045) { emit ->
      emit("PomodoroSession")
    }
  }

  public fun deleteById(id: String) {
    driver.execute(432_180_187, """DELETE FROM PomodoroSession WHERE id = ?""", 1) {
          bindString(0, id)
        }
    notifyQueries(432_180_187) { emit ->
      emit("PomodoroSession")
    }
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("PomodoroSession", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("PomodoroSession", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_306_602_156, """SELECT * FROM PomodoroSession WHERE id = ?""", mapper,
        1) {
      bindString(0, id)
    }

    override fun toString(): String = "PomodoroSession.sq:selectById"
  }

  private inner class SelectByDateQuery<out T : Any>(
    public val `value`: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("PomodoroSession", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("PomodoroSession", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_514_073_343,
        """SELECT * FROM PomodoroSession WHERE DATE(startTime) = ? ORDER BY startTime DESC""",
        mapper, 1) {
      bindString(0, value)
    }

    override fun toString(): String = "PomodoroSession.sq:selectByDate"
  }
}
