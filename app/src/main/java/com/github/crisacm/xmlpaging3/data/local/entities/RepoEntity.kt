package com.github.crisacm.xmlpaging3.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos")
data class RepoEntity(
  @PrimaryKey var id: String,
  var name: String,
  var username: String
)
