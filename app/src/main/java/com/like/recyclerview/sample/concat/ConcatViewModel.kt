package com.like.recyclerview.sample.concat

import androidx.lifecycle.ViewModel
import com.like.common.util.successIfAllSuccess

class ConcatViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    val loadAfterResult = LoadAfterDataSource(PAGE_SIZE).pagingResult()
    val LoadAfterWithHeadersResult = LoadAfterWithHeadersDataSource(PAGE_SIZE).pagingResult()
    val loadBeforeResult = LoadBeforeDataSource(PAGE_SIZE).pagingResult()

    private val itemDataSource = ItemDataSource()
    private val headerDataSource = HeaderDataSource()

    suspend fun getHeadersAndItems() = successIfAllSuccess(headerDataSource::load, itemDataSource::load)

    suspend fun getItems() = itemDataSource.load()
}
