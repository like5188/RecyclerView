package com.like.recyclerview.sample.paging

import androidx.lifecycle.ViewModel

class PagingViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    private val loadAfterPagingResult = LoadAfterPagingDataSource(PAGE_SIZE).result()
    private val loadBeforePagingResult = LoadBeforePagingDataSource(PAGE_SIZE).result()

    fun getResult() = loadAfterPagingResult
}