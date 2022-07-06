package com.like.recyclerview.sample.paging3.repository.inDb

import android.content.Context
import com.like.common.util.Logger
import com.like.common.util.isInternetAvailable
import com.like.recyclerview.sample.paging3.db.BannerDao
import com.like.recyclerview.sample.paging3.vo.BannerInfo
import com.like.recyclerview.sample.paging3.api.RetrofitUtils
import com.like.recyclerview.sample.paging3.util.IDbHelper

class DbBannerInfoDataSource(private val context: Context, private val bannerDao: BannerDao) {
    private val mDbHelper = object : IDbHelper<BannerInfo?> {
        override suspend fun loadFromDb(isRefresh: Boolean): BannerInfo? {
            Logger.e("BannerDbDataSource loadFromDb")
            val data = bannerDao.getAll()
            if (data.isEmpty()) {
                return null
            }
            return BannerInfo().apply {
                banners = data
            }
        }

        override fun shouldFetch(isRefresh: Boolean, result: BannerInfo?): Boolean {
            return context.isInternetAvailable() && (result?.banners.isNullOrEmpty() || isRefresh)
        }

        override suspend fun fetchFromNetworkAndSaveToDb(isRefresh: Boolean) {
            Logger.e("BannerDbDataSource fetchFromNetworkAndSaveToDb")
            val data = RetrofitUtils.retrofitApi.getBanner().getDataIfSuccess()
            if (!data.isNullOrEmpty()) {
                if (isRefresh) {
                    bannerDao.clear()
                }
                bannerDao.insert(*data.toTypedArray())
            }
        }
    }

    suspend fun load(isRefresh: Boolean = false): BannerInfo? {
        return mDbHelper.load(isRefresh)
    }

}