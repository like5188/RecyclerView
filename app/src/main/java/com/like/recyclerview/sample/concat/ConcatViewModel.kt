package com.like.recyclerview.sample.concat

import androidx.lifecycle.ViewModel
import com.like.recyclerview.sample.model.Item

class ConcatViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 5
    }

    val loadAfterResult = LoadAfterDataSource(PAGE_SIZE).result()
    val loadBeforeResult = LoadBeforeDataSource(PAGE_SIZE).result()
    private val loadResult = LoadDataSource()

    suspend fun getData(): List<Item> {
        return loadResult.load()
    }
}