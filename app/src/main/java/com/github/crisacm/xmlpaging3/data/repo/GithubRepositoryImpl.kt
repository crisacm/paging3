package com.github.crisacm.xmlpaging3.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.github.crisacm.xmlpaging3.data.api.pagingSource.GithubRepoPagingSources
import com.github.crisacm.xmlpaging3.data.api.service.GithubApi
import com.github.crisacm.xmlpaging3.data.local.AppDatabase
import com.github.crisacm.xmlpaging3.data.mapper.toDomain
import com.github.crisacm.xmlpaging3.domain.model.Repo
import com.github.crisacm.xmlpaging3.domain.repo.GithubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GithubRepositoryImpl
  @Inject
  constructor(
    private val api: GithubApi,
    private val appDatabase: AppDatabase,
  ) : GithubRepository {
    override fun fetchReposByUsername(username: String): Flow<PagingData<Repo>> =
      Pager(
        pagingSourceFactory = { GithubRepoPagingSources(api, appDatabase, username) },
        config = PagingConfig(pageSize = 10),
      ).flow

    override fun getReposByUsername(username: String): Flow<PagingData<Repo>> =
      Pager(
        pagingSourceFactory = { appDatabase.repoDao().getAllByPS(username) },
        config = PagingConfig(pageSize = 10),
      ).flow.map { it.map { obj -> obj.toDomain() } }
  }
