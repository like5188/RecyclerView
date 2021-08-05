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
        showEmpty: (ResultType) -> Boolean,
        onData: suspend (ResultType) -> Unit,
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
     * 分页
     */
    suspend fun <ResultType> collect(
        recyclerView: RecyclerView,
        isLoadAfter: Boolean,
        result: Result<ResultType>,
        showEmpty: (ResultType) -> Boolean,
        showLoadMoreEnd: (ResultType) -> Boolean,
        onData: suspend (ResultType) -> Unit,
        onLoadMore: suspend (ResultType) -> Int,
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
                            if (isLoadAfter) {
                                onData(data)
                                mAdapter.add(loadMoreAdapter)
                            } else {
                                mAdapter.add(loadMoreAdapter)
                                onData(data)
                            }
                            loadMoreAdapter.reload()
                            loadMoreAdapter.onComplete()
                            if (isLoadAfter) {
                                recyclerView.scrollToTop()
                            } else {
                                recyclerView.scrollToBottom()
                            }
                        }
                    }
                    requestType is RequestType.After || requestType is RequestType.Before -> {
                        if (showLoadMoreEnd(data)) {
                            loadMoreAdapter.onEnd()
                        } else {
                            val insertedItemCount = onLoadMore(data)
                            if (!isLoadAfter) {
                                recyclerView.keepPosition(insertedItemCount, 1)
                            }
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
