package com.like.recyclerview.sample.paging3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

class PagingViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    // initialLoadSize 默认为 PAGE_SIZE*3，所以这里需要设置一下。
    val itemFlow = Pager(PagingConfig(PAGE_SIZE, prefetchDistance = 1, initialLoadSize = PAGE_SIZE)) {
        ItemPagingSource()
    }.flow.cachedIn(viewModelScope)
}
