package com.like.recyclerview.sample.paging3.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.like.recyclerview.sample.paging3.data.db.Db
import com.like.recyclerview.sample.paging3.dataSource.memory.ArticlePagingSource
import com.like.recyclerview.sample.paging3.dataSource.memory.BannerDataSource
import com.like.recyclerview.sample.paging3.dataSource.db.PagingRemoteMediator
import com.like.recyclerview.sample.paging3.dataSource.memory.TopArticleDataSource

class PagingRepository(
    private val db: Db,
    private val bannerDataSource: BannerDataSource,
    private val topArticleDataSource: TopArticleDataSource
) {
    companion object {
        const val PAGE_SIZE = 10
    }

    // initialLoadSize 默认为 PAGE_SIZE*3，所以这里需要设置一下。
    val articleFlow = Pager(PagingConfig(PAGE_SIZE, prefetchDistance = 1, initialLoadSize = PAGE_SIZE)) {
        ArticlePagingSource()
    }.flow

    @OptIn(ExperimentalPagingApi::class)
    val dbArticleFlowFlow = Pager(
        PagingConfig(PAGE_SIZE, prefetchDistance = 1, initialLoadSize = PAGE_SIZE),
        remoteMediator = PagingRemoteMediator(db, bannerDataSource, topArticleDataSource)
    ) {
        db.articleDao().pagingSource()
    }.flow

    suspend fun getBanner() = bannerDataSource.load()
    suspend fun getTopArticle() = topArticleDataSource.load()
}