package com.like.recyclerview.sample.paging

class PagingRepository(
    private val appendPagingDataSource: AppendPagingDataSource,
    private val prependPagingDataSource: PrependPagingDataSource
) {
    fun getResult() = appendPagingDataSource.result()
}