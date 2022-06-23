package com.like.recyclerview.sample.paging3.dataSource.db

import android.content.Context
import com.like.common.util.Logger
import com.like.common.util.isInternetAvailable
import com.like.recyclerview.sample.paging3.data.db.TopArticleDao
import com.like.recyclerview.sample.paging3.data.model.TopArticle
import com.like.recyclerview.sample.paging3.data.netWork.RetrofitUtils
import com.like.recyclerview.sample.paging3.util.IDbHelper

class TopArticleDbDataSource(private val context: Context, private val topArticleEntityDao: TopArticleDao) {
    private val mDbHelper = object : IDbHelper<List<TopArticle>?> {
        override suspend fun loadFromDb(isRefresh: Boolean): List<TopArticle>? {
            Logger.w("TopArticleDbDataSource loadFromDb")
            return topArticleEntityDao.getAll()
        }

        override fun shouldFetch(isRefresh: Boolean, result: List<TopArticle>?): Boolean {
            return context.isInternetAvailable() && (result.isNullOrEmpty() || isRefresh)
        }

        override suspend fun fetchFromNetworkAndSaveToDb(isRefresh: Boolean) {
            Logger.w("TopArticleDbDataSource fetchFromNetworkAndSaveToDb")
            val data = RetrofitUtils.retrofitApi.getTopArticle().getDataIfSuccess()
            if (!data.isNullOrEmpty()) {
                if (isRefresh) {
                    topArticleEntityDao.clear()
                }
                topArticleEntityDao.insert(*data.toTypedArray())
            }
        }
    }

    suspend fun load(isRefresh: Boolean = false): List<TopArticle>? {
        return mDbHelper.load(isRefresh)
    }

}