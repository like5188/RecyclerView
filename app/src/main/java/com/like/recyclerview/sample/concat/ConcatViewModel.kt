package com.like.recyclerview.sample.concat

import androidx.lifecycle.ViewModel
import com.like.common.util.successIfOneSuccess
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.concat.repository.HeaderDataSource
import com.like.recyclerview.sample.concat.repository.ItemDataSource
import com.like.recyclerview.sample.concat.repository.LoadAfterDataSource
import com.like.recyclerview.sample.concat.repository.LoadBeforeDataSource

class ConcatViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    val loadAfterResult = LoadAfterDataSource(PAGE_SIZE).pagingResult()
    val LoadAfterWithHeadersResult = LoadAfterWithHeadersDataSource(PAGE_SIZE).pagingResult()
    val loadBeforeResult = LoadBeforeDataSource(PAGE_SIZE).pagingResult()

    private val itemDataSource = ItemDataSource()
    private val headerDataSource = HeaderDataSource()

    suspend fun getHeaders() = headerDataSource.load()

    suspend fun getItems() = itemDataSource.load()

    suspend fun getHeadersAndItems(): List<IRecyclerViewItem>? {
        val result = mutableListOf<IRecyclerViewItem>()
        successIfOneSuccess(headerDataSource::load, itemDataSource::load).forEach {
            if (it is List<*>) {
                it.forEach {
                    result.add(it as IRecyclerViewItem)
                }
            }
        }
        return result
    }

}
