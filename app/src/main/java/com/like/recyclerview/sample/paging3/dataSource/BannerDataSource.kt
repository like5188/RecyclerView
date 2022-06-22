package com.like.recyclerview.sample.paging3.dataSource

import com.like.recyclerview.sample.paging3.data.model.BannerInfo
import com.like.recyclerview.sample.paging3.data.netWork.RetrofitUtils

class BannerDataSource {

    suspend fun load(): BannerInfo? {
        val result = RetrofitUtils.retrofitApi.getBanner().getDataIfSuccess()
        return if (result.isNullOrEmpty()) {
            null
        } else {
            BannerInfo().apply {
                bannerEntities = result
            }
        }
    }

}