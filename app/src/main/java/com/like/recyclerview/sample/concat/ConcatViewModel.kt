package com.like.recyclerview.sample.concat

import androidx.lifecycle.ViewModel
import com.like.recyclerview.sample.concat.repository.HeaderDataSource
import com.like.recyclerview.sample.concat.repository.ItemDataSource
import com.like.recyclerview.sample.concat.repository.LoadAfterDataSource
import com.like.recyclerview.sample.concat.repository.LoadBeforeDataSource

class ConcatViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    val loadAfterResult = LoadAfterDataSource(PAGE_SIZE).pagingResult()
    val loadBeforeResult = LoadBeforeDataSource(PAGE_SIZE).pagingResult()

    private val itemDataSource = ItemDataSource()
    private val headerDataSource = HeaderDataSource()

    suspend fun getHeaders() = headerDataSource.load()

    suspend fun getItems() = itemDataSource.load()
}
