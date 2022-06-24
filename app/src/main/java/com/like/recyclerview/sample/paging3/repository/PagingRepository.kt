package com.like.recyclerview.sample.paging3.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import com.like.recyclerview.sample.paging3.data.db.Db
import com.like.recyclerview.sample.paging3.dataSource.db.ArticleRemoteMediator
import com.like.recyclerview.sample.paging3.dataSource.db.BannerDbDataSource
import com.like.recyclerview.sample.paging3.dataSource.db.TopArticleDbDataSource
import com.like.recyclerview.sample.paging3.dataSource.memory.ArticlePagingSource
import com.like.recyclerview.sample.paging3.dataSource.memory.BannerDataSource
import com.like.recyclerview.sample.paging3.dataSource.memory.TopArticleDataSource
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@OptIn(KoinApiExtension::class, ExperimentalPagingApi::class)
class PagingRepository(
    private val db: Db,
    private val bannerDataSource: BannerDataSource,
    private val bannerDbDataSource: BannerDbDataSource,
    private val topArticleDataSource: TopArticleDataSource,
    private val topArticleDbDataSource: TopArticleDbDataSource
) : KoinComponent {
    val articleFlow = Pager(get()) {
        get<ArticlePagingSource>()
    }.flow

    val dbArticleFlowFlow = Pager(
        get(),
        remoteMediator = get<ArticleRemoteMediator>()
    ) {
        db.articleDao().pagingSource()
    }.flow

    suspend fun getBanner() = bannerDataSource.load()
    suspend fun getTopArticle() = topArticleDataSource.load()
    suspend fun getDbBanner(isRefresh: Boolean) = bannerDbDataSource.load(isRefresh)
    suspend fun getDbTopArticle(isRefresh: Boolean) = topArticleDbDataSource.load(isRefresh)
}