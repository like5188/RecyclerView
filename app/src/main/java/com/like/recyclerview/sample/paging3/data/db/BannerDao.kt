package com.like.recyclerview.sample.paging3.data.db

import androidx.room.Dao
import androidx.room.Query
import com.like.recyclerview.sample.paging3.data.model.BannerInfo

@Dao
interface BannerDao : BaseDao<BannerInfo.Banner> {
    @Query("DELETE FROM Banner")
    suspend fun clear()

    @Query("SELECT * FROM Banner ORDER BY id ASC")
    suspend fun getAll(): List<BannerInfo.Banner>
}