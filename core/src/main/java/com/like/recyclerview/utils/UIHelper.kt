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
        listAdapter: AbstractAdapter<*, ValueInList>,
        emptyAdapter: AbstractAdapter<*, *>? = null,
        errorAdapter: AbstractErrorAdapter<*, *>? = null,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
    ) {
        result.bind(
            onData = {
                mAdapter.removeAllExcludeAndAdd(listAdapter)
                listAdapter.clear()
                listAdapter.addAllToEnd(it)
            },
            onEmpty = {
                emptyAdapter?.apply {
                    mAdapter.removeAllExcludeAndAdd(this)
                }
            },
            onError = {
                errorAdapter?.apply {
                    mAdapter.removeAllExcludeAndAdd(this)
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
        listAdapter: AbstractAdapter<*, ValueInList>,
        loadMoreAdapter: AbstractLoadMoreAdapter<*, *>,
        emptyAdapter: AbstractAdapter<*, *>? = null,
        errorAdapter: AbstractErrorAdapter<*, *>? = null,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
        onFailed: (suspend (RequestType, Throwable) -> Unit)? = null,
        onSuccess: (suspend (RequestType, List<ValueInList>?) -> Unit)? = null,
    ) = withContext(Dispatchers.Main) {
        val flow = result.bind(
            onInitialOrRefresh = {
                if (isLoadAfter) {
                    mAdapter.removeAllExcludeAndAdd(listAdapter, loadMoreAdapter)
                } else {
                    mAdapter.removeAllExcludeAndAdd(loadMoreAdapter, listAdapter)
                }
                listAdapter.clear()
                listAdapter.addAllToEnd(it)
                loadMoreAdapter.reload()// 为了触发加载更多，避免在数据量太少时，不能多次触发加载更多。
                loadMoreAdapter.onComplete()
                if (isLoadAfter) {
                    recyclerView.scrollToTop()
                } else {
                    recyclerView.scrollToBottom()
                }
            },
            onLoadMore = {
                if (isLoadAfter) {
                    listAdapter.addAllToEnd(it)
                } else {
                    listAdapter.addAllToStart(it)
                    recyclerView.keepPosition(it.size, 1)
                }
                loadMoreAdapter.reload()// 为了触发加载更多，避免在数据量太少时，不能多次触发加载更多。
                loadMoreAdapter.onComplete()
            },
            onLoadMoreEnd = { loadMoreAdapter.onEnd() },
            onLoadMoreError = { loadMoreAdapter.onError(it) },
            onInitialOrRefreshEmpty = {
                emptyAdapter?.apply {
                    mAdapter.removeAllExcludeAndAdd(this)
                }
            },
            onInitialError = {
                errorAdapter?.apply {
                    mAdapter.removeAllExcludeAndAdd(this)
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
