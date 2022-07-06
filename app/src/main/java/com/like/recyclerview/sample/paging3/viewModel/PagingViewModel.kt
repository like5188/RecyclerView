package com.like.recyclerview.sample.paging3.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.like.recyclerview.sample.paging3.repository.PagingRepository

class PagingViewModel(private val pagingRepository: PagingRepository) : ViewModel() {

    val articleFlow = pagingRepository.articleFlow.cachedIn(viewModelScope)

    val dbArticleFlow = pagingRepository.dbArticleFlow.cachedIn(viewModelScope)

    fun getBannerInfoFlow() = pagingRepository.getBannerInfoFlow()

    fun getDbBannerInfoFlow(isRefresh: Boolean) = pagingRepository.getDbBannerInfoFlow(isRefresh)

    fun getTopArticleFlow() = pagingRepository.getTopArticleFlow()

    fun getDbTopArticleFlow(isRefresh: Boolean) = pagingRepository.getDbTopArticleFlow(isRefresh)

}