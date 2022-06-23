package com.like.recyclerview.sample.paging3.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.like.recyclerview.sample.paging3.data.model.Article

@Dao
interface ArticleDao : BaseDao<Article> {
    @Query("DELETE FROM Article")
    suspend fun clear()

    @Query("SELECT * FROM Article")
    suspend fun getAll(): List<Article>

    @Query("SELECT * FROM Article")
    fun pagingSource(): PagingSource<Int, Article>

}