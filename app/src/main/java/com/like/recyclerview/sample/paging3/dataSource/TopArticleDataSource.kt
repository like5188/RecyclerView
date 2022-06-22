package com.like.recyclerview.sample.paging3.dataSource

import com.like.recyclerview.sample.paging3.data.model.TopArticle
import com.like.recyclerview.sample.paging3.data.netWork.RetrofitUtils

class TopArticleDataSource {

    suspend fun load(): List<TopArticle>? {
        return RetrofitUtils.retrofitApi.getTopArticle().getDataIfSuccess()
    }

}