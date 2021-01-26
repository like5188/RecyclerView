package com.like.recyclerview.sample.paging

import androidx.lifecycle.ViewModel

class PagingViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    val loadAfterPagingResult = LoadAfterPagingDataSource(PAGE_SIZE).result()
    val loadBeforePagingResult = LoadBeforePagingDataSource(PAGE_SIZE).result()

}