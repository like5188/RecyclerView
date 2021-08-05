package com.like.recyclerview.sample.concat

import androidx.lifecycle.ViewModel
import com.like.common.util.successIfAllSuccess

class ConcatViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 2
    }

    val loadAfterResult = LoadAfterDataSource(PAGE_SIZE).result()
    val LoadAfterWithHeadersResult = LoadAfterWithHeadersDataSource(PAGE_SIZE).result()
    val loadBeforeResult = LoadBeforeDataSource(PAGE_SIZE).result()

    private val itemResult = ItemDataSource()
    private val headerResult = HeaderDataSource()

    suspend fun getHeadersAndItems() = successIfAllSuccess(headerResult::load, itemResult::load)

    suspend fun getItems() = itemResult.load()
}
