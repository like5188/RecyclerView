package com.like.recyclerview.sample.concat

import com.like.recyclerview.sample.model.Item
import kotlinx.coroutines.delay

class LoadDataSource {
    private var i = 0

    suspend fun load(): List<Item> {
        delay(1000)
        return when (i++) {
            0 -> throw RuntimeException("load error")
            1 -> emptyList()
            else -> {
                (0 until 10).map {
                    Item(
                        id = it,
                        name = "name $it",
                        des = "des $it"
                    )
                }
            }
        }
    }

}
