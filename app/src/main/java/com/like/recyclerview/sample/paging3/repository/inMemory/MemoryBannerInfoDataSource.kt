package com.like.recyclerview.sample.paging3.repository.inMemory

import com.like.recyclerview.sample.paging3.vo.BannerInfo
import com.like.recyclerview.sample.paging3.api.RetrofitUtils

class MemoryBannerInfoDataSource {

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