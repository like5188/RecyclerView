package com.like.recyclerview.sample.paging3.repository.inDb

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import com.like.recyclerview.sample.paging3.db.Db
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@OptIn(KoinApiExtension::class, ExperimentalPagingApi::class)
class DbPagingRepository(
    private val db: Db,
    private val bannerInfoDbDataSource: BannerInfoDbDataSource,
    private val topArticleDbDataSource: TopArticleDbDataSource
) : KoinComponent {

    val dbArticleFlow = Pager(
        get(),
        remoteMediator = get<ArticleRemoteMediator>()
    ) {
        db.articleDao().pagingSource()
    }.flow

    fun getDbBannerInfoFlow(isRefresh: Boolean) = flow {
        emit(bannerInfoDbDataSource.load(isRefresh))
    }

    fun getDbTopArticleFlow(isRefresh: Boolean) = flow {
        emit(topArticleDbDataSource.load(isRefresh))
    }
}