package com.like.recyclerview.sample.paging3.dataSource

import android.content.Context
import com.like.common.util.isInternetAvailable
import com.like.recyclerview.sample.paging3.data.db.TopArticleEntityDao
import com.like.recyclerview.sample.paging3.data.model.TopArticleEntity
import com.like.recyclerview.sample.paging3.data.netWork.RetrofitUtils
import com.like.recyclerview.sample.paging3.util.IDbHelper

class TopArticleDbDataSource(private val context: Context, private val topArticleEntityDao: TopArticleEntityDao) {
    private val mDbHelper = object : IDbHelper<List<TopArticleEntity>?> {
        override suspend fun loadFromDb(isRefresh: Boolean): List<TopArticleEntity>? {
            return topArticleEntityDao.getAll()
        }

        override fun shouldFetch(isRefresh: Boolean, result: List<TopArticleEntity>?): Boolean {
            return context.isInternetAvailable() && (result.isNullOrEmpty() || isRefresh)
        }

        override suspend fun fetchFromNetworkAndSaveToDb(isRefresh: Boolean) {
            val data = RetrofitUtils.retrofitApi.getTopArticle().getDataIfSuccess()
            if (!data.isNullOrEmpty()) {
                if (isRefresh) {
                    topArticleEntityDao.clear()
                }
                topArticleEntityDao.insert(*data.toTypedArray())
            }
        }
    }

    suspend fun load(isRefresh: Boolean = false): List<TopArticleEntity>? {
        return mDbHelper.load(isRefresh)
    }

}