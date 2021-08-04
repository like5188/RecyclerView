package com.like.recyclerview.sample.concat

import com.like.recyclerview.sample.model.Header1
import com.like.recyclerview.sample.model.Header2
import kotlinx.coroutines.delay

class HeaderDataSource {

    suspend fun load(): List<Any> {
        delay(1000)
        return (0 until 1).map {
            Header1(
                name = "header1 $it",
            )
        } + (0 until 1).map {
            Header2(
                name = "header2 $it",
            )
        }
    }

}
