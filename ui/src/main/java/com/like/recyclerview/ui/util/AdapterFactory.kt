package com.like.recyclerview.ui.util

import com.like.recyclerview.ui.loadmore.LoadMoreAdapter
import com.like.recyclerview.ui.loadmore.LoadMoreItem

object AdapterFactory {

    fun createLoadMoreAdapter() = LoadMoreAdapter().apply {
        addToEnd(LoadMoreItem())
    }

}
