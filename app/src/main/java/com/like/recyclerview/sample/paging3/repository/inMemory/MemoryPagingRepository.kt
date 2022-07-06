package com.like.recyclerview.sample.paging3.repository.inMemory

import androidx.paging.Pager
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@OptIn(KoinApiExtension::class)
class MemoryPagingRepository(
    private val bannerInfoDataSource: BannerInfoDataSource,
    private val topArticleDataSource: TopArticleDataSource,
) : KoinComponent {

    val articleFlow = Pager(get()) {
        get<ArticlePagingSource>()
    }.flow

    fun getBannerInfoFlow() = flow {
        emit(bannerInfoDataSource.load())
    }

    fun getTopArticleFlow() = flow {
        emit(topArticleDataSource.load())
    }

}