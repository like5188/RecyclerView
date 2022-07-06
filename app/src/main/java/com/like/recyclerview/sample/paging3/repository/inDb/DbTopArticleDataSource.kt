package com.like.recyclerview.sample.paging3.repository.inDb

import android.content.Context
import com.like.common.util.Logger
import com.like.common.util.isInternetAvailable
import com.like.recyclerview.sample.paging3.api.Api
import com.like.recyclerview.sample.paging3.db.Db
import com.like.recyclerview.sample.paging3.util.IDbHelper
import com.like.recyclerview.sample.paging3.vo.TopArticle

class DbTopArticleDataSource(
    private val context: Context,
    db: Db,
    private val api: Api
) {
    private val topArticleEntityDao = db.topArticleDao()
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
            val data = api.getTopArticle().getDataIfSuccess()
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