package com.like.recyclerview.sample.concat

import androidx.lifecycle.ViewModel

class ConcatViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    val loadAfterResult = LoadAfterDataSource(PAGE_SIZE).result()
    val loadBeforeResult = LoadBeforeDataSource(PAGE_SIZE).result()
    private val loadResult = LoadDataSource()

    suspend fun getItems(): List<Any> {
        return loadResult.load()
    }

    private val headerResult = HeaderDataSource()

    suspend fun getHeaders(): List<Any> {
        return headerResult.load()
    }
}
