package co.rahees.productivitymonitoring.database

import kotlin.Long
import kotlin.String

public data class TodoItem(
  public val id: String,
  public val title: String,
  public val description: String,
  public val isCompleted: Long,
  public val createdAt: String,
  public val completedAt: String?,
)
