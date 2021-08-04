package com.like.recyclerview.sample.concat

import com.like.recyclerview.sample.model.Header1
import com.like.recyclerview.sample.model.Header2
import com.like.recyclerview.sample.model.Item1
import com.like.recyclerview.sample.model.Item2

object DataFactory {
    fun createHeader(index: Int): Any {
        return if (index % 3 == 0) {
            Header1(
                name = "Header1 $index",
            )
        } else {
            Header2(
                name = "Header2 $index",
            )
        }
    }

    fun createItem(index: Int): Any {
        return if (index % 3 == 0) {
            Item1(
                id = index,
                name = "Item1 $index",
                des = "des $index"
            )
        } else {
            Item2(
                id = index,
                name = "Item2 $index",
                des = "des $index"
            )
        }
    }
}