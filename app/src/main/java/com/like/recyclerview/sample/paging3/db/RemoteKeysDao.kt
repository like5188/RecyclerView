package com.like.recyclerview.sample.paging3.db

import androidx.room.Dao
import androidx.room.Query
import com.like.recyclerview.sample.paging3.vo.RemoteKeysEntity

@Dao
interface RemoteKeysDao : BaseDao<RemoteKeysEntity> {
    @Query("SELECT * FROM RemoteKeysEntity where remoteName = :name ")
    suspend fun getRemoteKeys(name: String): RemoteKeysEntity?

    @Query("DELETE FROM RemoteKeysEntity where remoteName = :name")
    suspend fun clearRemoteKeys(name: String)
}