package com.like.recyclerview.sample.paging3.dataSource.inMemory

import com.like.recyclerview.sample.paging3.data.model.TopArticleEntity
import com.like.recyclerview.sample.paging3.data.netWork.RetrofitUtils

class TopArticleDataSource {

    suspend fun load(): List<TopArticleEntity>? {
        return RetrofitUtils.retrofitApi.getTopArticle().getDataIfSuccess()
    }

}