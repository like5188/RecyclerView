package com.like.recyclerview.sample.paging3.db

import androidx.room.Dao
import androidx.room.Query
import com.like.recyclerview.sample.paging3.vo.BannerInfo

@Dao
interface BannerDao : BaseDao<BannerInfo.Banner> {
    @Query("DELETE FROM Banner")
    suspend fun clear()

    @Query("SELECT * FROM Banner")
    suspend fun getAll(): List<BannerInfo.Banner>
}