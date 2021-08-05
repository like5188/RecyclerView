package com.like.recyclerview.sample.concat

import com.like.recyclerview.model.IRecyclerViewItem
import kotlinx.coroutines.delay

class ItemDataSource {
    private var i = 0

    suspend fun load(): List<IRecyclerViewItem>? {
        delay(1000)
        return when (i++) {
            0 -> throw RuntimeException("load error")
            1 -> emptyList()
            else -> {
                (0 until 10).map {
                    DataFactory.createItem(it)
                }
            }
        }
    }

}
