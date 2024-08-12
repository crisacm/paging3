package com.github.crisacm.xmlpaging3.data.mapper

import com.github.crisacm.xmlpaging3.data.api.models.RepoResponses
import com.github.crisacm.xmlpaging3.data.local.entities.RepoEntity
import com.github.crisacm.xmlpaging3.domain.model.Repo

fun RepoResponses.toDomain(): Repo {
  return Repo(name = name)
}

fun RepoResponses.toEntity(): RepoEntity {
  return RepoEntity(id = id, name = name, username = owner.login)
}

fun RepoEntity.toDomain(): Repo {
  return Repo(name = name)
}