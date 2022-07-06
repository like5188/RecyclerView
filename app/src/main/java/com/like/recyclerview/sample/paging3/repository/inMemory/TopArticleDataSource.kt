package com.like.recyclerview.sample.paging3.repository.inMemory

import com.like.recyclerview.sample.paging3.vo.TopArticle
import com.like.recyclerview.sample.paging3.api.RetrofitUtils

class TopArticleDataSource {

    suspend fun load(): List<TopArticle>? {
        return RetrofitUtils.retrofitApi.getTopArticle().getDataIfSuccess()
    }

}