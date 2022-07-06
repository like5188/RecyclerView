package com.like.recyclerview.sample.paging3.repository.inMemory

import androidx.paging.Pager
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@OptIn(KoinApiExtension::class)
class MemoryPagingRepository(
    private val memoryBannerInfoDataSource: MemoryBannerInfoDataSource,
    private val memoryTopArticleDataSource: MemoryTopArticleDataSource,
) : KoinComponent {

    val memoryArticleFlow = Pager(get()) {
        get<MemoryArticlePagingSource>()
    }.flow

    fun getMemoryBannerInfoFlow() = flow {
        emit(memoryBannerInfoDataSource.load())
    }

    fun getMemoryTopArticleFlow() = flow {
        emit(memoryTopArticleDataSource.load())
    }

}