package com.like.recyclerview.sample.concat

import kotlinx.coroutines.delay

class HeaderDataSource {

    suspend fun load(): List<Any> {
        delay(1000)
        return (0 .. 5).map {
            DataFactory.createHeader(it)
        }
    }

}
