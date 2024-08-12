package com.github.crisacm.xmlpaging3.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RepoResponses(
  @field:Json(name = "id") var id: String,
  @field:Json(name = "name") val name: String,
  @field:Json(name = "owner") val owner: Owner
)

@JsonClass(generateAdapter = true)
data class Owner(
  @field:Json(name = "login") val login: String
)
