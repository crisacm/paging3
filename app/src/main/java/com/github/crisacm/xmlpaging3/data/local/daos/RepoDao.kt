package com.github.crisacm.xmlpaging3.data.local.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.crisacm.xmlpaging3.data.local.entities.RepoEntity

@Dao
interface RepoDao {

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(list: List<RepoEntity>)

  @Query("DELETE FROM repos")
  suspend fun clearRepos()

   @Query("SELECT * FROM repos WHERE username = :username")
   fun getAllBy(username: String): PagingSource<Int, RepoEntity>
}
