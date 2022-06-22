package com.like.recyclerview.sample.paging3.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.like.recyclerview.sample.paging3.data.model.ArticleEntity

@Dao
interface ArticleEntityDao : BaseDao<ArticleEntity> {
    @Query("DELETE FROM ArticleEntity")
    suspend fun clear()

    @Query("SELECT * FROM ArticleEntity ORDER BY id ASC")
    suspend fun getAll(): List<ArticleEntity>

    @Query("SELECT * FROM ArticleEntity ORDER BY id ASC limit :pageSize offset :offset")
    suspend fun getPage(offset: Int, pageSize: Int): List<ArticleEntity>
}