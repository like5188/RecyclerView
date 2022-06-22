package com.like.recyclerview.sample.paging3.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.like.recyclerview.sample.paging3.dataSource.BannerDataSource
import com.like.recyclerview.sample.paging3.dataSource.PagingDataSource
import com.like.recyclerview.sample.paging3.dataSource.TopArticleDataSource

class PagingViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 10
    }

    // initialLoadSize 默认为 PAGE_SIZE*3，所以这里需要设置一下。
    val pagingFlow = Pager(PagingConfig(PAGE_SIZE, prefetchDistance = 1, initialLoadSize = PAGE_SIZE)) {
        PagingDataSource(BannerDataSource(), TopArticleDataSource())
    }.flow.cachedIn(viewModelScope)

}