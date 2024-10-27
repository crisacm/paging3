package com.github.crisacm.xmlpaging3.data.local.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.crisacm.xmlpaging3.data.local.entities.RepoEntity

@Dao
interface RepoDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(repo: RepoEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(list: List<RepoEntity>)

  @Query("DELETE FROM repos")
  suspend fun clearRepos()

  @Query("SELECT * FROM repos WHERE username = :username ORDER BY id ASC LIMIT :limit OFFSET :offset")
  fun getAllBy(username: String, limit: Int, offset: Int): List<RepoEntity>

  @Query("SELECT * FROM repos WHERE username = :username ORDER BY id ASC")
  fun getAllByRaw(username: String): List<RepoEntity>

  @Query("SELECT * FROM repos WHERE username = :username")
  fun getAllByPS(username: String): PagingSource<Int, RepoEntity>
}
