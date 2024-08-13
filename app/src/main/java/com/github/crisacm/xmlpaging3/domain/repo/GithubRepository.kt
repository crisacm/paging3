package com.github.crisacm.xmlpaging3.domain.repo

import androidx.paging.PagingData
import com.github.crisacm.xmlpaging3.domain.model.Repo
import kotlinx.coroutines.flow.Flow

interface GithubRepository {

  fun fetchReposByUsername(username: String): Flow<PagingData<Repo>>

  fun getReposByUsername(username: String): Flow<PagingData<Repo>>
}
