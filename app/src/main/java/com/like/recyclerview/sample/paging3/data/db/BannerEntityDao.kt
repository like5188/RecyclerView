package com.like.recyclerview.sample.paging3.data.db

import androidx.room.Dao
import androidx.room.Query
import com.like.recyclerview.sample.paging3.data.model.BannerInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface BannerEntityDao : BaseDao<BannerInfo.BannerEntity> {
    @Query("DELETE FROM BannerEntity")
    suspend fun clear()

    @Query("SELECT * FROM BannerEntity ORDER BY id ASC")
    fun getAll(): Flow<List<BannerInfo.BannerEntity>>
}