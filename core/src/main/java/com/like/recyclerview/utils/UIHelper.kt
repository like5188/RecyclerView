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
    suspend fun <ResultType> collect(
        result: (suspend () -> ResultType),
        onData: suspend (ResultType) -> Unit,
        showEmpty: (ResultType) -> Boolean,
        emptyAdapter: AbstractAdapter<*, *>? = null,
        errorAdapter: AbstractErrorAdapter<*, *>? = null,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
    ) {
        result.bind(
            onData = {
                if (showEmpty(it)) {
                    mAdapter.clear()
                    mAdapter.add(emptyAdapter)
                } else {
                    mAdapter.clear()
                    onData(it)
                }
            },
            onError = {
                mAdapter.clear()
                mAdapter.add(errorAdapter)
                errorAdapter?.onError(it)
            },
            show = show,
            hide = hide,
        )
    }

    /**
     * 往后分页
     */
    suspend fun <ResultType> collectForLoadAfter(
        recyclerView: RecyclerView,
        result: Result<ResultType>,
        onData: suspend (ResultType) -> Unit,
        onLoadMore: suspend (ResultType) -> Unit,
        showEmpty: (ResultType) -> Boolean,
        showLoadMoreEnd: (ResultType) -> Boolean,
        loadMoreAdapter: AbstractLoadMoreAdapter<*, *>,
        emptyAdapter: AbstractAdapter<*, *>? = null,
        errorAdapter: AbstractErrorAdapter<*, *>? = null,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
    ) = withContext(Dispatchers.Main) {
        val flow = result.bind(
            onData = { requestType, data ->
                when {
                    requestType is RequestType.Initial || requestType is RequestType.Refresh -> {
                        if (showEmpty(data)) {
                            mAdapter.clear()
                            mAdapter.add(emptyAdapter)
                        } else {
                            mAdapter.clear()
                            if (!showLoadMoreEnd(data)) {
                                onData(data)
                                mAdapter.add(loadMoreAdapter)
                                loadMoreAdapter.reload()
                                loadMoreAdapter.onComplete()
                            } else {
                                onData(data)
                            }
                            recyclerView.scrollToTop()
                        }
                    }
                    requestType is RequestType.After || requestType is RequestType.Before -> {
                        if (showLoadMoreEnd(data)) {
                            loadMoreAdapter.onEnd()
                        } else {
                            onLoadMore(data)
                            loadMoreAdapter.reload()
                            loadMoreAdapter.onComplete()
                        }
                    }
                }
            },
            onError = { requestType, throwable ->
                when {
                    requestType is RequestType.Initial -> {
                        mAdapter.clear()
                        mAdapter.add(errorAdapter)
                        errorAdapter?.onError(throwable)
                    }
                    requestType is RequestType.After || requestType is RequestType.Before -> {
                        loadMoreAdapter.onError(throwable)
                    }
                }
            },
            show = show,
            hide = hide,
        )
        launch {
            flow.collect()
        }
        launch {
            result.initial()
        }
    }

    /**
     * 往前分页
     */
    suspend fun <ValueInList> collectForLoadBefore(
        recyclerView: RecyclerView,
        result: Result<List<ValueInList>?>,
        listAdapter: AbstractAdapter<*, ValueInList>,
        loadMoreAdapter: AbstractLoadMoreAdapter<*, *>,
        emptyAdapter: AbstractAdapter<*, *>? = null,
        errorAdapter: AbstractErrorAdapter<*, *>? = null,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
    ) = withContext(Dispatchers.Main) {
        val flow = result.bind(
            onData = { requestType, data ->
                when {
                    requestType is RequestType.Initial || requestType is RequestType.Refresh -> {
                        if (data.isNullOrEmpty()) {
                            mAdapter.clear()
                            mAdapter.add(emptyAdapter)
                        } else {
                            mAdapter.clear()
                            mAdapter.addAll(loadMoreAdapter, listAdapter)
                            listAdapter.clear()
                            listAdapter.addAllToEnd(data)
                            loadMoreAdapter.reload()
                            loadMoreAdapter.onComplete()
                            recyclerView.scrollToBottom()
                        }
                    }
                    requestType is RequestType.After || requestType is RequestType.Before -> {
                        if (data.isNullOrEmpty()) {
                            loadMoreAdapter.onEnd()
                        } else {
                            listAdapter.addAllToStart(data)
                            recyclerView.keepPosition(data.size, 1)
                            loadMoreAdapter.reload()
                            loadMoreAdapter.onComplete()
                        }
                    }
                }
            },
            onError = { requestType, throwable ->
                when {
                    requestType is RequestType.Initial -> {
                        mAdapter.clear()
                        mAdapter.add(errorAdapter)
                        errorAdapter?.onError(throwable)
                    }
                    requestType is RequestType.After || requestType is RequestType.Before -> {
                        loadMoreAdapter.onError(throwable)
                    }
                }
            },
            show = show,
            hide = hide,
        )
        launch {
            flow.collect()
        }
        launch {
            result.initial()
        }
    }

}
