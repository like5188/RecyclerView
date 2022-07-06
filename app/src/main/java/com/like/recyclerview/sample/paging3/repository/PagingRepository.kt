package com.like.recyclerview.sample.paging3.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import com.like.recyclerview.sample.paging3.data.db.Db
import com.like.recyclerview.sample.paging3.dataSource.db.ArticleRemoteMediator
import com.like.recyclerview.sample.paging3.dataSource.db.BannerInfoDbDataSource
import com.like.recyclerview.sample.paging3.dataSource.db.TopArticleDbDataSource
import com.like.recyclerview.sample.paging3.dataSource.memory.ArticlePagingSource
import com.like.recyclerview.sample.paging3.dataSource.memory.BannerInfoDataSource
import com.like.recyclerview.sample.paging3.dataSource.memory.TopArticleDataSource
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@OptIn(KoinApiExtension::class, ExperimentalPagingApi::class)
class PagingRepository(
    private val db: Db,
    private val bannerInfoDataSource: BannerInfoDataSource,
    private val bannerInfoDbDataSource: BannerInfoDbDataSource,
    private val topArticleDataSource: TopArticleDataSource,
    private val topArticleDbDataSource: TopArticleDbDataSource
) : KoinComponent {

    val articleFlow = Pager(get()) {
        get<ArticlePagingSource>()
    }.flow

    val dbArticleFlow = Pager(
        get(),
        remoteMediator = get<ArticleRemoteMediator>()
    ) {
        db.articleDao().pagingSource()
    }.flow

    fun getBannerInfoFlow() = flow {
        emit(bannerInfoDataSource.load())
    }

    fun getTopArticleFlow() = flow {
        emit(topArticleDataSource.load())
    }

    fun getDbBannerInfoFlow(isRefresh: Boolean) = flow {
        emit(bannerInfoDbDataSource.load(isRefresh))
    }

    fun getDbTopArticleFlow(isRefresh: Boolean) = flow {
        emit(topArticleDbDataSource.load(isRefresh))
    }
}