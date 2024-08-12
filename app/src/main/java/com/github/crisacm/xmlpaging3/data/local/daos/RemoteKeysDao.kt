package com.github.crisacm.xmlpaging3.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.crisacm.xmlpaging3.data.local.entities.RemoteKeys

@Dao
interface RemoteKeysDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(list: List<RemoteKeys>)

  @Query("DELETE FROM remote_keys")
  suspend fun clearRemoteKeys()

  @Query("SELECT * FROM remote_keys WHERE repoId = :repoId")
  suspend fun remoteKeysRepoId(repoId: String): RemoteKeys?
}
