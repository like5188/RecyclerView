package com.like.recyclerview.sample.concat

import androidx.lifecycle.ViewModel
import com.like.recyclerview.model.IRecyclerViewItem

class ConcatViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    val loadAfterResult = LoadAfterDataSource(PAGE_SIZE).result()
    val loadBeforeResult = LoadBeforeDataSource(PAGE_SIZE).result()
    private val loadResult = LoadDataSource()

    suspend fun getItems(): List<IRecyclerViewItem> {
        return loadResult.load()
    }

    private val headerResult = HeaderDataSource()

    suspend fun getHeaders(): List<IRecyclerViewItem> {
        return headerResult.load()
    }
}
