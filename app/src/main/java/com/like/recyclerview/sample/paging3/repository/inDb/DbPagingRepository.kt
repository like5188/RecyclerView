package com.like.recyclerview.sample.paging3.repository.inDb

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.like.recyclerview.sample.paging3.db.Db
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalPagingApi::class)
class DbPagingRepository(
    private val db: Db,
    private val dbBannerInfoDataSource: DbBannerInfoDataSource,
    private val dbTopArticleDataSource: DbTopArticleDataSource,
    articleRemoteMediator: ArticleRemoteMediator,
    pagingConfig: PagingConfig
) {

    val dbArticleFlow = Pager(
        pagingConfig,
        remoteMediator = articleRemoteMediator
    ) {
        db.articleDao().pagingSource()
    }.flow

    fun getDbBannerInfoFlow(isRefresh: Boolean) = flow {
        emit(dbBannerInfoDataSource.load(isRefresh))
    }

    fun getDbTopArticleFlow(isRefresh: Boolean) = flow {
        emit(dbTopArticleDataSource.load(isRefresh))
    }
}