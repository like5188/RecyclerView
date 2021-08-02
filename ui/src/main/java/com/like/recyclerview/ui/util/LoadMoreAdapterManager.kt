package com.like.recyclerview.ui.util

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.RequestType
import com.like.paging.Result
import com.like.paging.util.bind
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.ui.adapter.EmptyAdapter
import com.like.recyclerview.ui.adapter.ErrorAdapter
import com.like.recyclerview.ui.adapter.LoadMoreAdapter
import com.like.recyclerview.ui.model.EmptyItem
import com.like.recyclerview.ui.model.ErrorItem
import com.like.recyclerview.ui.model.LoadMoreItem
import com.like.recyclerview.utils.keepPosition
import com.like.recyclerview.utils.scrollToBottom
import com.like.recyclerview.utils.scrollToTop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoadMoreAdapterManager<ValueInList>(
    private val coroutineScope: CoroutineScope,
    private val recyclerView: RecyclerView,
    private val isLoadAfter: Boolean = true,
    private val result: Result<List<ValueInList>?>,
    private val contentAdapters: List<AbstractAdapter<*, ValueInList>>,
) {
    private val mAdapter = ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())
    private val loadMoreAdapter = LoadMoreAdapter {
        coroutineScope.launch {
            if (isLoadAfter) {
                result.loadAfter?.invoke()
            } else {
                result.loadBefore?.invoke()
            }
        }
    }
    private val emptyAdapter = EmptyAdapter()
    private val errorAdapter = ErrorAdapter()

    fun getAdapter(): ConcatAdapter = mAdapter

    fun collect(
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
                loadMoreAdapter.clear()
                loadMoreAdapter.addToEnd(LoadMoreItem())
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
                emptyAdapter.clear()
                emptyAdapter.addToEnd(EmptyItem())
            },
            onInitialError = {
                for (contentAdapter in contentAdapters) {
                    mAdapter.removeAdapter(contentAdapter)
                }
                mAdapter.removeAdapter(loadMoreAdapter)
                mAdapter.removeAdapter(emptyAdapter)
                mAdapter.addAdapter(errorAdapter)
                errorAdapter.clear()
                errorAdapter.addToEnd(ErrorItem())
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