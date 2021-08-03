package com.like.recyclerview.utils

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.RequestType
import com.like.paging.Result
import com.like.paging.util.bind
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.adapter.AbstractErrorAdapter
import com.like.recyclerview.adapter.AbstractLoadMoreAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 辅助搜集分页或者不分页数据，并绑定到界面。
 */
class UIHelper(private val mAdapter: ConcatAdapter) {

    /**
     * 不分页
     */
    suspend fun <ValueInList> collect(
        result: (suspend () -> List<ValueInList>?),
        contentAdapter: AbstractAdapter<*, ValueInList>,
        emptyAdapter: AbstractAdapter<*, *>? = null,
        errorAdapter: AbstractErrorAdapter<*, *>? = null,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
    ) {
        result.bind(
            onData = {
                mAdapter.removeAllExclude(contentAdapter)
                if (!mAdapter.contains(contentAdapter)) {
                    mAdapter.addAdapter(contentAdapter)
                }
                contentAdapter.clear()
                contentAdapter.addAllToEnd(it)
            },
            onEmpty = {
                emptyAdapter?.apply {
                    mAdapter.removeAllExclude(this)
                    if (!mAdapter.contains(this)) {
                        mAdapter.addAdapter(this)
                    }
                }
            },
            onError = {
                errorAdapter?.apply {
                    mAdapter.removeAllExclude(this)
                    if (!mAdapter.contains(this)) {
                        mAdapter.addAdapter(this)
                    }
                    onError(it)
                }
            },
            show = show,
            hide = hide,
        )
    }

    /**
     * 分页
     */
    suspend fun <ValueInList> collect(
        recyclerView: RecyclerView,
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
    ) = withContext(Dispatchers.Main) {
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
        launch {
            flow.collect()
        }
        launch {
            result.initial()
        }
    }
}
