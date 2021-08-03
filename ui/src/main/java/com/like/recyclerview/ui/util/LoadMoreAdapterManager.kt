package com.like.recyclerview.ui.util

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.RequestType
import com.like.paging.Result
import com.like.paging.util.bind
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.adapter.AbstractErrorAdapter
import com.like.recyclerview.adapter.AbstractLoadMoreAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoadMoreAdapterManager(
    private val coroutineScope: CoroutineScope,
    private val recyclerView: RecyclerView,
) {
    private val mAdapter = ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())

    fun getAdapter(): ConcatAdapter = mAdapter

    fun <ValueInList> collect(
        isLoadAfter: Boolean,
        result: Result<List<ValueInList>?>,
        contentAdapters: List<AbstractAdapter<*, ValueInList>>,
        emptyAdapter: AbstractAdapter<*, *>? = null,
        errorAdapter: AbstractErrorAdapter<*, *>? = null,
        loadMoreAdapter: AbstractLoadMoreAdapter<*, *>? = null,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
        onFailed: (suspend (RequestType, Throwable) -> Unit)? = null,
        onSuccess: (suspend (RequestType, List<ValueInList>?) -> Unit)? = null,
    ) {
        val flow = result.bind(
            onInitialOrRefresh = {
                emptyAdapter?.apply {
                    mAdapter.removeAdapter(this)
                }
                errorAdapter?.apply {
                    mAdapter.removeAdapter(this)
                }
                if (isLoadAfter) {
                    for (contentAdapter in contentAdapters) {
                        mAdapter.addAdapter(contentAdapter)
                        contentAdapter.clear()
                        contentAdapter.addAllToEnd(it)
                    }
                    loadMoreAdapter?.apply {
                        mAdapter.addAdapter(this)
                        onComplete()
                    }
                } else {
                    loadMoreAdapter?.apply {
                        mAdapter.addAdapter(this)
                        onComplete()
                    }
                    for (contentAdapter in contentAdapters) {
                        mAdapter.addAdapter(contentAdapter)
                        contentAdapter.clear()
                        contentAdapter.addAllToEnd(it)
                    }
                }
                if (isLoadAfter) {
                    recyclerView.scrollToTop()
                } else {
                    recyclerView.scrollToBottom()
                }
            },
            onLoadMore = {
                if (isLoadAfter) {
                    for (contentAdapter in contentAdapters) {
                        contentAdapter.addAllToEnd(it)
                    }
                } else {
                    for (contentAdapter in contentAdapters) {
                        contentAdapter.addAllToStart(it)
                    }
                    recyclerView.keepPosition(it.size, 1)
                }
                loadMoreAdapter?.onComplete()
            },
            onLoadMoreEnd = { loadMoreAdapter?.onEnd() },
            onLoadMoreError = { loadMoreAdapter?.onError(it) },
            onInitialOrRefreshEmpty = {
                for (contentAdapter in contentAdapters) {
                    mAdapter.removeAdapter(contentAdapter)
                }
                loadMoreAdapter?.apply {
                    mAdapter.removeAdapter(this)
                }
                errorAdapter?.apply {
                    mAdapter.removeAdapter(this)
                }
                emptyAdapter?.apply {
                    mAdapter.addAdapter(this)
                }
            },
            onInitialError = {
                for (contentAdapter in contentAdapters) {
                    mAdapter.removeAdapter(contentAdapter)
                }
                loadMoreAdapter?.apply {
                    mAdapter.removeAdapter(this)
                }
                emptyAdapter?.apply {
                    mAdapter.removeAdapter(this)
                }
                errorAdapter?.apply {
                    mAdapter.addAdapter(this)
                    onError(it)
                }
            },
            show = show,
            hide = hide,
            onFailed = onFailed,
            onSuccess = onSuccess,
        )
        coroutineScope.launch {
            flow.collect()
        }
        coroutineScope.launch {
            result.initial()
        }
    }
}