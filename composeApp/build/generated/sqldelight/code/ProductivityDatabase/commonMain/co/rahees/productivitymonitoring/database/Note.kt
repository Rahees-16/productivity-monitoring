package co.rahees.productivitymonitoring.database

import kotlin.String

public data class Note(
  public val id: String,
  public val content: String,
  public val createdAt: String,
  public val lastModified: String,
)
