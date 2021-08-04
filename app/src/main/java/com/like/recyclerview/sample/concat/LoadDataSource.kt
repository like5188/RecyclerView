package com.like.recyclerview.sample.concat

import kotlinx.coroutines.delay

class LoadDataSource {
    private var i = 0

    suspend fun load(): List<Any> {
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
