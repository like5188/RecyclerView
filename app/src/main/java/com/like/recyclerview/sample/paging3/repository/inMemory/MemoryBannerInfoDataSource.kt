package com.like.recyclerview.sample.paging3.repository.inMemory

import com.like.recyclerview.sample.paging3.api.Api
import com.like.recyclerview.sample.paging3.vo.BannerInfo

class MemoryBannerInfoDataSource(private val api: Api) {

    suspend fun load(): BannerInfo? {
        val data = api.getBanner().getDataIfSuccess()
        return if (data.isNullOrEmpty()) {
            null
        } else {
            BannerInfo().apply {
                banners = data
            }
        }
    }

}