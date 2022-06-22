package com.like.recyclerview.sample.paging3.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.like.recyclerview.sample.paging3.data.model.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleEntityDao : BaseDao<ArticleEntity> {
    @Query("DELETE FROM ArticleEntity")
    suspend fun clear()

    @Query("SELECT * FROM ArticleEntity ORDER BY id ASC")
    fun getAll(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM ArticleEntity ORDER BY id ASC limit :pageSize offset :offset")
    suspend fun getPage(offset: Int, pageSize: Int): List<ArticleEntity>

    @Query("SELECT * FROM ArticleEntity ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, ArticleEntity>

}