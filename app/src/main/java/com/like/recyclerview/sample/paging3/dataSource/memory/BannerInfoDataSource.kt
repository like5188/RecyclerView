package com.like.recyclerview.sample.paging3.dataSource.memory

import com.like.recyclerview.sample.paging3.data.model.BannerInfo
import com.like.recyclerview.sample.paging3.data.netWork.RetrofitUtils

class BannerInfoDataSource {

    suspend fun load(): BannerInfo? {
        val data = RetrofitUtils.retrofitApi.getBanner().getDataIfSuccess()
        return if (data.isNullOrEmpty()) {
            null
        } else {
            BannerInfo().apply {
                banners = data
            }
        }
    }

}