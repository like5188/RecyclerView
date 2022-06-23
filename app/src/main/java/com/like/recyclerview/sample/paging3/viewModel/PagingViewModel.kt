package com.like.recyclerview.sample.paging3.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.like.recyclerview.sample.paging3.repository.PagingRepository
import kotlinx.coroutines.flow.flow

class PagingViewModel(private val pagingRepository: PagingRepository) : ViewModel() {

    val articleFlow = pagingRepository.articleFlow.cachedIn(viewModelScope)

    val dbArticleFlowFlow = pagingRepository.dbArticleFlowFlow.cachedIn(viewModelScope)

    fun getBannerInfoFlow(isRefresh: Boolean) = flow {
        emit(pagingRepository.getDbBanner(isRefresh))
    }

    fun getTopArticleFlow(isRefresh: Boolean) = flow {
        emit(pagingRepository.getDbTopArticle(isRefresh))
    }

}