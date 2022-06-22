package com.like.recyclerview.sample.paging3.dataSource

import android.content.Context
import com.like.common.util.isInternetAvailable
import com.like.recyclerview.sample.paging3.data.db.BannerEntityDao
import com.like.recyclerview.sample.paging3.data.model.BannerInfo
import com.like.recyclerview.sample.paging3.data.netWork.RetrofitUtils
import com.like.recyclerview.sample.paging3.util.IDbHelper

class BannerDbDataSource(private val context: Context, private val bannerEntityDao: BannerEntityDao) {
    private val mDbHelper = object : IDbHelper<List<BannerInfo>?> {
        override suspend fun loadFromDb(isRefresh: Boolean): List<BannerInfo>? {
            val data = bannerEntityDao.getAll()
            if (data.isEmpty()) {
                return null
            }
            val bannerInfo = BannerInfo().apply {
                bannerEntities = data
            }
            return listOf(bannerInfo)
        }

        override fun shouldFetch(isRefresh: Boolean, result: List<BannerInfo>?): Boolean {
            return context.isInternetAvailable() && (result.isNullOrEmpty() || isRefresh)
        }

        override suspend fun fetchFromNetworkAndSaveToDb(isRefresh: Boolean) {
            val data = RetrofitUtils.retrofitApi.getBanner().getDataIfSuccess()
            if (!data.isNullOrEmpty()) {
                if (isRefresh) {
                    bannerEntityDao.clear()
                }
                bannerEntityDao.insert(*data.toTypedArray())
            }
        }
    }

    suspend fun load(isRefresh: Boolean = false): List<BannerInfo>? {
        return mDbHelper.load(isRefresh)
    }

}