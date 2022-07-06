package com.like.recyclerview.sample.paging3.db

import androidx.room.Dao
import androidx.room.Query
import com.like.recyclerview.sample.paging3.vo.TopArticle

@Dao
interface TopArticleDao : BaseDao<TopArticle> {

    @Query("DELETE FROM TopArticle")
    suspend fun clear()

    @Query("SELECT * FROM TopArticle")
    suspend fun getAll(): List<TopArticle>
}