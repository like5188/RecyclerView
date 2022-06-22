package com.like.recyclerview.sample.paging3.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.like.recyclerview.sample.paging3.data.model.BannerInfo

@Dao
interface BannerEntityDao : BaseDao<BannerInfo.BannerEntity> {
    @Query("DELETE FROM BannerEntity")
    suspend fun clear()

    @Query("SELECT * FROM BannerEntity ORDER BY id ASC")
    suspend fun getAll(): List<BannerInfo.BannerEntity>
}