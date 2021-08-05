package com.like.recyclerview.sample.concat

import androidx.lifecycle.ViewModel
import com.like.common.util.successIfAllSuccess

class ConcatViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    val loadAfterResult = LoadAfterDataSource(PAGE_SIZE).result()
    val loadBeforeResult = LoadBeforeDataSource(PAGE_SIZE).result()

    private val itemResult = ItemDataSource()
    private val headerResult = HeaderDataSource()

    suspend fun getData() = successIfAllSuccess(headerResult::load, itemResult::load)

}
