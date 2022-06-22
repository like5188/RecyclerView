package com.like.recyclerview.sample.paging3.data.db

import androidx.room.Dao
import androidx.room.Query
import com.like.recyclerview.sample.paging3.data.model.BannerInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface BannerDao : BaseDao<BannerInfo.Banner> {
    @Query("DELETE FROM Banner")
    suspend fun clear()

    @Query("SELECT * FROM Banner ORDER BY id ASC")
    fun getAll(): Flow<List<BannerInfo.Banner>>
}