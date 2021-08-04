package com.like.recyclerview.sample.concat

import com.like.recyclerview.model.IRecyclerViewItem
import kotlinx.coroutines.delay

class HeaderDataSource {

    suspend fun load(): List<IRecyclerViewItem> {
        delay(1000)
        return (0 .. 5).map {
            DataFactory.createHeader(it)
        }
    }

}
