@file:OptIn(ExperimentalPagingApi::class)

package com.github.crisacm.xmlpaging3.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.github.crisacm.xmlpaging3.data.api.service.GithubApi
import com.github.crisacm.xmlpaging3.data.local.AppDatabase
import com.github.crisacm.xmlpaging3.data.local.entities.RemoteKeys
import com.github.crisacm.xmlpaging3.data.local.entities.RepoEntity
import com.github.crisacm.xmlpaging3.data.mapper.toDomain
import com.github.crisacm.xmlpaging3.data.mapper.toEntity
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

private const val DEFAULT_PAGE_INDEX = 1

class RepoMediator(
  private val api: GithubApi,
  private val appDatabase: AppDatabase,
  private val username: String
) : RemoteMediator<Int, RepoEntity>() {

  override suspend fun load(loadType: LoadType, state: PagingState<Int, RepoEntity>): MediatorResult {
    val pageKeyData = getKeyPageData(loadType, state)
    val page = when (pageKeyData) {
      is MediatorResult.Success -> {
        return pageKeyData
      }

      else -> {
        pageKeyData as Int
      }
    }

    try {
      val response = api.fetchRepos(username, page, state.config.pageSize)
      val isEndOfList = response.isEmpty()

      appDatabase.withTransaction {
        // Here we clear all tables in the database
        if (loadType == LoadType.REFRESH) {
          appDatabase.remoteKeysDao().clearRemoteKeys()
          appDatabase.repoDao().clearRepos()
        }

        val prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1
        val nextKey = if (isEndOfList) null else page + 1
        val keys = response.map {
          RemoteKeys(repoId = it.id, prevKey = prevKey, nextKey = nextKey)
        }

        appDatabase.remoteKeysDao().insert(keys)
        appDatabase.repoDao().insert(response.map { it.toEntity() })
      }

      return MediatorResult.Success(endOfPaginationReached = isEndOfList)
    } catch (e: IOException) {
      return MediatorResult.Error(e)
    } catch (e: HttpException) {
      return MediatorResult.Error(e)
    }
  }

  suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, RepoEntity>): Any? {
    return when (loadType) {
      LoadType.REFRESH -> {
        val remoteKeys = getClosesRemoteKey(state)
        remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE_INDEX
      }

      LoadType.APPEND -> {
        val remoteKeys = getLastRemoteKey(state)
          ?: throw InvalidObjectException("Remote key should hot be null for $loadType")
        remoteKeys.nextKey
      }

      LoadType.PREPEND -> {
        val remoteKeys = getFirstRemoteKey(state)
          ?: throw InvalidObjectException("Invalid state, key should not be null")
        // end of list condition reached
        remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
        remoteKeys.prevKey
      }
    }
  }

  private suspend fun getFirstRemoteKey(state: PagingState<Int, RepoEntity>): RemoteKeys? {
    return state.pages
      .firstOrNull { it.data.isNotEmpty() }
      ?.data?.firstOrNull()
      ?.let { repo -> appDatabase.remoteKeysDao().remoteKeysRepoId(repo.id) }
  }

  private suspend fun getLastRemoteKey(state: PagingState<Int, RepoEntity>): RemoteKeys? {
    return state.pages
      .lastOrNull { it.data.isNotEmpty() }
      ?.data?.lastOrNull()
      ?.let { repo -> appDatabase.remoteKeysDao().remoteKeysRepoId(repo.id) }
  }

  private suspend fun getClosesRemoteKey(state: PagingState<Int, RepoEntity>): RemoteKeys? {
    return state.anchorPosition?.let { pos ->
      state.closestItemToPosition(pos)?.id?.let { repoId ->
        appDatabase.remoteKeysDao().remoteKeysRepoId(repoId)
      }
    }
  }
}