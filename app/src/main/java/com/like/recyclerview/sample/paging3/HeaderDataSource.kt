package com.like.recyclerview.sample.paging3

import android.util.Log
import com.like.recyclerview.model.IRecyclerViewItem
import kotlinx.coroutines.delay

class HeaderDataSource {

    suspend fun load(): List<IRecyclerViewItem>? {
        Log.d("tag", "HeaderDataSource")
        delay(1000)
        return (0 .. 5).map {
            DataFactory.createHeader(it)
        }
    }

}
