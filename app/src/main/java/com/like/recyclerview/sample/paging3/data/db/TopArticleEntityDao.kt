package com.like.recyclerview.sample.paging3.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.like.recyclerview.sample.paging3.data.model.TopArticleEntity

@Dao
interface TopArticleEntityDao : BaseDao<TopArticleEntity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TopArticleEntity>)

    @Query("DELETE FROM TopArticleEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM TopArticleEntity ORDER BY id ASC")
    suspend fun getAll(): List<TopArticleEntity>
}