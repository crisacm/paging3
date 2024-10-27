package com.github.crisacm.xmlpaging3.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
  @PrimaryKey val repoId: String,
  val prevKey: Int?,
  val nextKey: Int?,
)
