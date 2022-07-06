package com.like.recyclerview.sample.paging3.repository.inDb

import android.content.Context
import com.like.common.util.Logger
import com.like.common.util.isInternetAvailable
import com.like.recyclerview.sample.paging3.api.Api
import com.like.recyclerview.sample.paging3.db.Db
import com.like.recyclerview.sample.paging3.util.IDbHelper
import com.like.recyclerview.sample.paging3.vo.BannerInfo

class DbBannerInfoDataSource(
    private val context: Context,
    db: Db,
    private val api: Api
) {
    private val bannerDao = db.bannerDao()
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
            val data = api.getBanner().getDataIfSuccess()
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