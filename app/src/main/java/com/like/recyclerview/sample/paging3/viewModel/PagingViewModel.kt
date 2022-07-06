package com.like.recyclerview.sample.paging3.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.like.recyclerview.sample.paging3.repository.inDb.DbPagingRepository
import com.like.recyclerview.sample.paging3.repository.inMemory.MemoryPagingRepository

class PagingViewModel(
    private val dbPagingRepository: DbPagingRepository,
    private val memoryPagingRepository: MemoryPagingRepository
) : ViewModel() {

    val memoryArticleFlow = memoryPagingRepository.memoryArticleFlow.cachedIn(viewModelScope)

    val dbArticleFlow = dbPagingRepository.dbArticleFlow.cachedIn(viewModelScope)

    fun getMemoryBannerInfoFlow() = memoryPagingRepository.getMemoryBannerInfoFlow()

    fun getDbBannerInfoFlow(isRefresh: Boolean) = dbPagingRepository.getDbBannerInfoFlow(isRefresh)

    fun getMemoryTopArticleFlow() = memoryPagingRepository.getMemoryTopArticleFlow()

    fun getDbTopArticleFlow(isRefresh: Boolean) = dbPagingRepository.getDbTopArticleFlow(isRefresh)

}