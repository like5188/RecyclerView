package com.like.recyclerview.sample.concat

import android.util.Log
import com.like.recyclerview.model.IRecyclerViewItem
import kotlinx.coroutines.delay

class ItemDataSource {
    private var i = 0

    suspend fun load(): List<IRecyclerViewItem>? {
        Log.d("tag", "ItemDataSource")
        delay(1000)
        return when (i++) {
            0 -> throw RuntimeException("load error 0")
            1 -> throw RuntimeException("load error 1")
            2 -> throw RuntimeException("load error 2")
            3 -> emptyList()
            4 -> throw RuntimeException("load error 4")
            6 -> throw RuntimeException("load error 6")
            else -> {
                (0 until 10).map {
                    DataFactory.createItem(it)
                }
            }
        }
    }

}
