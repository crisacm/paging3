package com.github.crisacm.xmlpaging3.data.api.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.crisacm.xmlpaging3.data.api.service.GithubApi
import com.github.crisacm.xmlpaging3.data.local.AppDatabase
import com.github.crisacm.xmlpaging3.data.mapper.toDomain
import com.github.crisacm.xmlpaging3.data.mapper.toEntity
import com.github.crisacm.xmlpaging3.domain.model.Repo

private const val INITIAL_PAGE = 1

class GithubRepoPagingSources(
  private val api: GithubApi,
  private val appDatabase: AppDatabase,
  private val username: String
) : PagingSource<Int, Repo>() {

  override fun getRefreshKey(state: PagingState<Int, Repo>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }
  }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
    return try {
      val page = params.key ?: INITIAL_PAGE
      val response = api.fetchRepos(username, page, params.loadSize)
      appDatabase.repoDao().insert(response.map { it.toEntity() })

      LoadResult.Page(
        data = response.map { it.toDomain() },
        prevKey = if (page == INITIAL_PAGE) null else page - 1,
        nextKey = if (response.isEmpty()) null else page + 1
      )
    } catch (e: Exception) {
      LoadResult.Error(e)
    }
  }
}