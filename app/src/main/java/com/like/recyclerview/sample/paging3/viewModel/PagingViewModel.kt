package com.like.recyclerview.sample.paging3.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.like.recyclerview.sample.paging3.repository.PagingRepository
import kotlinx.coroutines.flow.flow

class PagingViewModel(pagingRepository: PagingRepository) : ViewModel() {

    val articleFlow = pagingRepository.articleFlow.cachedIn(viewModelScope)

    val dbArticleFlowFlow = pagingRepository.dbArticleFlowFlow.cachedIn(viewModelScope)

    val bannerInfoFlow = flow {
        emit(pagingRepository.getBanner())
    }

    val topArticleFlow = flow {
        emit(pagingRepository.getTopArticle())
    }

}