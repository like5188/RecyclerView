package com.like.recyclerview.sample.paging3

import com.like.recyclerview.model.IRecyclerViewItem

object DataFactory {

    fun createItem(index: Int): IRecyclerViewItem {
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