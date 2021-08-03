package com.like.recyclerview.ui.util

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.RequestType
import com.like.paging.Result
import com.like.paging.util.bind
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.adapter.AbstractLoadMoreAdapter
import com.like.recyclerview.utils.keepPosition
import com.like.recyclerview.utils.scrollToBottom
import com.like.recyclerview.utils.scrollToTop
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
        loadMoreAdapter: AbstractLoadMoreAdapter<*, *>,
        emptyAdapter: AbstractAdapter<*, *>,
        errorAdapter: AbstractAdapter<*, *>,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
        onFailed: (suspend (RequestType, Throwable) -> Unit)? = null,
        onSuccess: (suspend (RequestType, List<ValueInList>?) -> Unit)? = null,
    ) {
        val flow = result.bind(
            onInitialOrRefresh = {
                mAdapter.removeAdapter(emptyAdapter)
                mAdapter.removeAdapter(errorAdapter)
                if (isLoadAfter) {
                    for (contentAdapter in contentAdapters) {
                        mAdapter.addAdapter(contentAdapter)
                        contentAdapter.clear()
                        contentAdapter.addAllToEnd(it)
                    }
                    mAdapter.addAdapter(loadMoreAdapter)
                } else {
                    mAdapter.addAdapter(loadMoreAdapter)
                    for (contentAdapter in contentAdapters) {
                        mAdapter.addAdapter(contentAdapter)
                        contentAdapter.clear()
                        contentAdapter.addAllToEnd(it)
                    }
                }
                loadMoreAdapter.onComplete()
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
                loadMoreAdapter.onComplete()
            },
            onLoadMoreEnd = { loadMoreAdapter.onEnd() },
            onLoadMoreError = { loadMoreAdapter.onError(it) },
            onInitialOrRefreshEmpty = {
                for (contentAdapter in contentAdapters) {
                    mAdapter.removeAdapter(contentAdapter)
                }
                mAdapter.removeAdapter(loadMoreAdapter)
                mAdapter.removeAdapter(errorAdapter)
                mAdapter.addAdapter(emptyAdapter)
            },
            onInitialError = {
                for (contentAdapter in contentAdapters) {
                    mAdapter.removeAdapter(contentAdapter)
                }
                mAdapter.removeAdapter(loadMoreAdapter)
                mAdapter.removeAdapter(emptyAdapter)
                mAdapter.addAdapter(errorAdapter)
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