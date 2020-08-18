package com.like.recyclerview.sample.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class PagingViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    private val loadAfterPagingResult = LoadAfterPagingDataSource(viewModelScope, PAGE_SIZE).result()
    private val loadBeforePagingResult = LoadBeforePagingDataSource(viewModelScope, PAGE_SIZE).result()

    fun getResult() = loadAfterPagingResult
}