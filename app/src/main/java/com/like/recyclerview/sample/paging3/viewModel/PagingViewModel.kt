package com.like.recyclerview.sample.paging3.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.like.recyclerview.sample.paging3.repository.PagingRepository

class PagingViewModel(pagingRepository: PagingRepository) : ViewModel() {

    val pagingFlow = pagingRepository.pagingFlow.cachedIn(viewModelScope)

    val dbPagingFlow = pagingRepository.dbPagingFlow.cachedIn(viewModelScope)

}