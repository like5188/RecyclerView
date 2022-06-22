package com.like.recyclerview.sample.paging3.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.like.recyclerview.sample.paging3.data.model.BannerInfo

@Dao
interface BannerEntityDao : BaseDao<BannerInfo.BannerEntity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BannerInfo.BannerEntity>)

    @Query("DELETE FROM BannerEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM BannerEntity ORDER BY id ASC")
    suspend fun getAll(): List<BannerInfo.BannerEntity>
}