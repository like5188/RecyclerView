package com.like.recyclerview.sample.paging3.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.like.recyclerview.sample.paging3.data.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao : BaseDao<Article> {
    @Query("DELETE FROM Article")
    suspend fun clear()

    @Query("SELECT * FROM Article ORDER BY id ASC")
    fun getAll(): Flow<List<Article>>

    @Query("SELECT * FROM Article ORDER BY id ASC limit :pageSize offset :offset")
    suspend fun getPage(offset: Int, pageSize: Int): List<Article>

    @Query("SELECT * FROM Article ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, Article>

}