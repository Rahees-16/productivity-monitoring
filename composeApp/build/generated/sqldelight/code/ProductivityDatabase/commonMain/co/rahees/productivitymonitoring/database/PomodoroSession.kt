package co.rahees.productivitymonitoring.database

import kotlin.Long
import kotlin.String

public data class PomodoroSession(
  public val id: String,
  public val startTime: String,
  public val endTime: String?,
  public val duration: Long,
  public val isCompleted: Long,
  public val sessionType: String,
)
