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
    private val dbBannerInfoDataSource: DbBannerInfoDataSource,
    private val dbTopArticleDataSource: DbTopArticleDataSource
) : KoinComponent {

    val dbArticleFlow = Pager(
        get(),
        remoteMediator = get<ArticleRemoteMediator>()
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