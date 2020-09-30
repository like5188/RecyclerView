package com.like.recyclerview.sample.paging

class PagingRepository(
    private val loadAfterPagingDataSource: LoadAfterPagingDataSource,
    private val loadBeforePagingDataSource: LoadBeforePagingDataSource
) {
    fun getResult() = loadAfterPagingDataSource.result()
}