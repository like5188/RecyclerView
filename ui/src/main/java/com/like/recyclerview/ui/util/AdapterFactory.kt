package com.like.recyclerview.ui.util

import com.like.recyclerview.ui.empty.EmptyAdapter
import com.like.recyclerview.ui.empty.EmptyItem
import com.like.recyclerview.ui.error.ErrorAdapter
import com.like.recyclerview.ui.error.ErrorItem
import com.like.recyclerview.ui.loadmore.LoadMoreAdapter
import com.like.recyclerview.ui.loadmore.LoadMoreItem

object AdapterFactory {

    fun createEmptyAdapter() = EmptyAdapter().apply {
        addToEnd(EmptyItem())
    }

    fun createErrorAdapter() = ErrorAdapter().apply {
        addToEnd(ErrorItem())
    }

    fun createLoadMoreAdapter() = LoadMoreAdapter().apply {
        addToEnd(LoadMoreItem())
    }

}
