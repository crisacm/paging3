package com.github.crisacm.xmlpaging3.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.crisacm.xmlpaging3.data.local.daos.RemoteKeysDao
import com.github.crisacm.xmlpaging3.data.local.daos.RepoDao
import com.github.crisacm.xmlpaging3.data.local.entities.RemoteKeys
import com.github.crisacm.xmlpaging3.data.local.entities.RepoEntity

@Database(
  version = 1,
  entities = [
    RemoteKeys::class,
    RepoEntity::class,
  ],
  exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun remoteKeysDao(): RemoteKeysDao

  abstract fun repoDao(): RepoDao

  companion object {
    private const val DATABASE_NAME = "github-db"

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase =
      INSTANCE ?: synchronized(this) {
        INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
      }

    private fun buildDatabase(context: Context) =
      Room
        .databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          DATABASE_NAME,
        ).build()
  }
}
