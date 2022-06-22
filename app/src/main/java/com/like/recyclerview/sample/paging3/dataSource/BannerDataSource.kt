package com.like.recyclerview.sample.paging3.dataSource

import com.like.recyclerview.sample.paging3.data.model.BannerInfo
import com.like.recyclerview.sample.paging3.data.netWork.RetrofitUtils

class BannerDataSource {

    suspend fun load(): List<BannerInfo>? {
        val result = RetrofitUtils.retrofitApi.getBanner().getDataIfSuccess()
        return if (result.isNullOrEmpty()) {
            null
        } else {
            val bannerInfo = BannerInfo().apply {
                bannerEntities = result
            }
            listOf(bannerInfo)
        }
    }

}