package com.like.recyclerview.sample.paging3.data.db

import androidx.room.Dao
import androidx.room.Query
import com.like.recyclerview.sample.paging3.data.model.TopArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopArticleEntityDao : BaseDao<TopArticleEntity> {

    @Query("DELETE FROM TopArticleEntity")
    suspend fun clear()

    @Query("SELECT * FROM TopArticleEntity ORDER BY id ASC")
    fun getAll(): Flow<List<TopArticleEntity>>
}