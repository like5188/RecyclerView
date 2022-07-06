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

    val articleFlow = memoryPagingRepository.articleFlow.cachedIn(viewModelScope)

    val dbArticleFlow = dbPagingRepository.dbArticleFlow.cachedIn(viewModelScope)

    fun getBannerInfoFlow() = memoryPagingRepository.getBannerInfoFlow()

    fun getDbBannerInfoFlow(isRefresh: Boolean) = dbPagingRepository.getDbBannerInfoFlow(isRefresh)

    fun getTopArticleFlow() = memoryPagingRepository.getTopArticleFlow()

    fun getDbTopArticleFlow(isRefresh: Boolean) = dbPagingRepository.getDbTopArticleFlow(isRefresh)

}