package com.like.recyclerview.sample.paging3.data.db

import androidx.room.Dao
import androidx.room.Query
import com.like.recyclerview.sample.paging3.data.model.TopArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface TopArticleDao : BaseDao<TopArticle> {

    @Query("DELETE FROM TopArticle")
    suspend fun clear()

    @Query("SELECT * FROM TopArticle ORDER BY id ASC")
    fun getAll(): Flow<List<TopArticle>>
}