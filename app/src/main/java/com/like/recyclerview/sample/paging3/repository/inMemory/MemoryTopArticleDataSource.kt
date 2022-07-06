package com.like.recyclerview.sample.paging3.repository.inMemory

import com.like.recyclerview.sample.paging3.api.Api
import com.like.recyclerview.sample.paging3.vo.TopArticle

class MemoryTopArticleDataSource(private val api: Api) {

    suspend fun load(): List<TopArticle>? {
        return api.getTopArticle().getDataIfSuccess()
    }

}